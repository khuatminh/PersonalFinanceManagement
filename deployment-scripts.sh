#!/bin/bash

# ========================================
# Personal Finance Manager Deployment Script
# ========================================

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="finance-app"
APP_DIR="/opt/$APP_NAME"
SERVICE_USER="your-username"  # CHANGE THIS
DB_NAME="personal_finance_db"
DB_USER="financeapp"
DB_PASSWORD="StrongPassword123!"  # CHANGE THIS
DOMAIN_NAME="your-domain.com"  # CHANGE THIS
VPS_IP=$(curl -s ifconfig.me) || echo "your-vps-ip"

echo -e "${GREEN}🚀 Personal Finance Manager Deployment Script${NC}"
echo "========================================="

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check if running as root
if [[ $EUID -eq 0 ]]; then
    print_error "Please run this script as a non-root user with sudo privileges."
    exit 1
fi

print_status "Starting deployment for Personal Finance Manager..."

# 1. Update System
print_status "Updating system packages..."
sudo apt update && sudo apt upgrade -y

# 2. Install Required Packages
print_status "Installing required packages..."
sudo apt install curl wget unzip git htop openjdk-11-jdk maven mysql-server nginx -y

# 3. Configure Java
print_status "Configuring Java..."
if ! grep -q "JAVA_HOME" ~/.bashrc; then
    echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi
source ~/.bashrc

# 4. Configure MySQL
print_status "Configuring MySQL..."
sudo mysql -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
sudo mysql -e "CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASSWORD';"
sudo mysql -e "GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';"
sudo mysql -e "FLUSH PRIVILEGES;"

# 5. Create Application Directory
print_status "Creating application directory..."
sudo mkdir -p $APP_DIR
sudo chown $USER:$USER $APP_DIR

# 6. Prompt for Gemini API Key
echo -e "${YELLOW}⚠️  IMPORTANT: Please enter your Gemini API Key${NC}"
read -p "Enter your Gemini API Key (or press Enter to skip): " GEMINI_API_KEY

if [[ -n "$GEMINI_API_KEY" ]]; then
    echo "export GEMINI_API_KEY=$GEMINI_API_KEY" >> ~/.bashrc
    export GEMINI_API_KEY=$GEMINI_API_KEY
    print_status "Gemini API key configured"
else
    print_warning "No Gemini API key provided. AI features will be disabled."
fi

# 7. Deploy Application
print_status "Please upload your application files to $APP_DIR"
print_warning "Make sure your application.properties is configured correctly with:"
echo "  - Database URL: jdbc:mysql://localhost:3306/$DB_NAME"
echo "  - Database Username: $DB_USER"
echo "  - Database Password: $DB_PASSWORD"
echo ""
read -p "Press Enter to continue once files are uploaded..."

# 8. Build Application
print_status "Building application..."
cd $APP_DIR
mvn clean package -DskipTests

# 9. Create Systemd Service
print_status "Creating systemd service..."
sudo tee /etc/systemd/system/$APP_NAME.service > /dev/null <<EOF
[Unit]
Description=Personal Finance Manager Application
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=$SERVICE_USER
Group=$SERVICE_USER
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -jar $APP_DIR/target/personal-finance-manager-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=$APP_NAME

# Environment Variables
Environment="GEMINI_API_KEY=$GEMINI_API_KEY"
Environment="JAVA_OPTS=-Xmx512m -Xms256m"

[Install]
WantedBy=multi-user.target
EOF

# 10. Configure Nginx
print_status "Configuring Nginx..."
sudo tee /etc/nginx/sites-available/$APP_NAME > /dev/null <<EOF
server {
    listen 80;
    server_name $DOMAIN_NAME $VPS_IP;

    location / {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;

        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    client_max_body_size 10M;
}
EOF

# Enable Nginx site
sudo ln -sf /etc/nginx/sites-available/$APP_NAME /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# 11. Configure Firewall
print_status "Configuring firewall..."
sudo ufw --force enable
sudo ufw allow ssh
sudo ufw allow 80
sudo ufw allow 443

# 12. Start Services
print_status "Starting services..."
sudo systemctl daemon-reload
sudo systemctl enable $APP_NAME
sudo systemctl start $APP_NAME

sudo systemctl restart nginx

# 13. Setup SSL (if domain provided)
if [[ "$DOMAIN_NAME" != "your-domain.com" ]]; then
    print_status "Setting up SSL certificate for $DOMAIN_NAME..."
    sudo apt install certbot python3-certbot-nginx -y
    sudo certbot --nginx -d $DOMAIN_NAME --non-interactive --agree-tos --email admin@$DOMAIN_NAME
else
    print_warning "No domain provided. Skipping SSL setup."
    print_status "You can access your app at: http://$VPS_IP"
fi

# 14. Final Status Check
print_status "Checking deployment status..."

# Check application service
if sudo systemctl is-active --quiet $APP_NAME; then
    print_status "✅ Application service is running"
else
    print_error "❌ Application service is not running"
    sudo systemctl status $APP_NAME
fi

# Check Nginx
if sudo systemctl is-active --quiet nginx; then
    print_status "✅ Nginx is running"
else
    print_error "❌ Nginx is not running"
fi

# Check MySQL
if sudo systemctl is-active --quiet mysql; then
    print_status "✅ MySQL is running"
else
    print_error "❌ MySQL is not running"
fi

# Test application
if curl -s http://localhost:8080 > /dev/null; then
    print_status "✅ Application is responding on localhost:8080"
else
    print_error "❌ Application is not responding on localhost:8080"
fi

# 15. Final Instructions
echo ""
echo "========================================="
echo -e "${GREEN}🎉 Deployment Complete!${NC}"
echo "========================================="
echo ""
echo "Your Personal Finance Manager is now deployed!"
echo ""
if [[ "$DOMAIN_NAME" != "your-domain.com" ]]; then
    echo -e "${GREEN}📱 Access URLs:${NC}"
    echo "  • HTTPS: https://$DOMAIN_NAME"
    echo "  • HTTP: http://$DOMAIN_NAME (redirects to HTTPS)"
else
    echo -e "${GREEN}📱 Access URL:${NC}"
    echo "  • HTTP: http://$VPS_IP"
fi
echo ""
echo -e "${GREEN}🔧 Management Commands:${NC}"
echo "  • View logs: sudo journalctl -u $APP_NAME -f"
echo "  • Restart app: sudo systemctl restart $APP_NAME"
echo "  • Check status: sudo systemctl status $APP_NAME"
echo ""
echo -e "${YELLOW}⚠️  Important Notes:${NC}"
echo "  1. Change your default admin password"
echo "  2. Set up regular database backups"
echo "  3. Monitor application logs"
echo "  4. Keep the system updated"
echo ""
if [[ -n "$GEMINI_API_KEY" ]]; then
    echo "  ✅ AI Chat features are enabled"
else
    echo "  ❌ AI Chat features are disabled (no API key)"
fi
echo ""
print_status "Deployment script completed successfully!"