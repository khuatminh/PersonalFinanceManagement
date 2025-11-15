package com.finance.controller;

import com.finance.domain.User;
import com.finance.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * REST Controller for handling chatbot interactions.
 *
 * This controller provides API endpoints for processing user messages
 * through the AI-powered chatbot to automatically extract and save
 * financial transactions.
 *
 * @author Personal Finance Manager Team
 * @version 1.0.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Processes a user message to extract and save transaction information.
     *
     * @param request The chat request containing the user message
     * @param user The authenticated user
     * @return Response message indicating the result
     */
    @PostMapping
    public ResponseEntity<ChatResponse> processMessage(@Valid @RequestBody ChatRequest request) {
        try {
            // Create a default test user for demonstration purposes
            User actualUser = createTestUser();
            logger.info("Received chat message: {}", request.getMessage());

            // Process the message through chat service
            Map<String, String> result = chatService.processMessage(request.getMessage(), actualUser);

            ChatResponse response = new ChatResponse(result.get("message"));

            // Add additional fields if available
            if (result.containsKey("transactionId")) {
                response.setTransactionId(result.get("transactionId"));
            }
            if (result.containsKey("type")) {
                response.setType(result.get("type"));
            }
            if (result.containsKey("amount")) {
                response.setAmount(result.get("amount"));
            }
            if (result.containsKey("category")) {
                response.setCategory(result.get("category"));
            }

            logger.info("Successfully processed message for user: {}", actualUser.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing chat message: {}", e.getMessage(), e);
            ChatResponse errorResponse = new ChatResponse("❌ Đã xảy ra lỗi khi xử lý tin nhắn. Vui lòng thử lại sau.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Creates a test user for demonstration purposes when no authenticated user is available.
     *
     * @return A test user object
     */
    private User createTestUser() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        return testUser;
    }

    /**
     * Gets the current status of the chat service.
     *
     * @return Status information
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        try {
            Map<String, Object> status = chatService.getStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Error getting chat service status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get service status"));
        }
    }

    /**
     * Request DTO for chat messages.
     */
    public static class ChatRequest {

        @NotBlank(message = "Message cannot be blank")
        private String message;

        public ChatRequest() {}

        public ChatRequest(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * Response DTO for chat messages.
     */
    public static class ChatResponse {

        private String message;
        private String transactionId;
        private String type;
        private String amount;
        private String category;

        public ChatResponse() {}

        public ChatResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}