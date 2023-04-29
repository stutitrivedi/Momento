    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    // Message class representing a message sent by a user
    class Message {
        private final User sender;
        private final List<User> recipients;
        private String content;
        private final LocalDateTime timestamp;

        public Message(User sender, List<User> recipients, String content) {
            this.sender = sender;
            this.recipients = recipients;
            this.content = content;
            this.timestamp = LocalDateTime.now();
        }

        public User getSender() {
            return sender;
        }

        public List<User> getRecipients() {
            return recipients;
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        // Memento method to create a snapshot of the message
        public MessageMemento saveToMemento() {
            return new MessageMemento(content, timestamp);
        }

        // Memento method to restore a message from a snapshot
        public void restoreFromMemento(MessageMemento memento) {
            // Only restore the content, not the timestamp
            content = memento.getContent();
        }

        // toString method for debugging purposes
        public String toString() {
            return "timestamp is " + timestamp + ", Sender is  " + sender.getName() + ", Recepiant is  -> " + recipients + ", Contant is : " + content;
        }
    }

    // User class representing a user of the chat application
    class User {
        private final String name;
        private final ChatServer server;
        private final ChatHistory chatHistory;
        private Message lastSentMessage;

        public User(String name, ChatServer server) {
            this.name = name;
            this.server = server;
            this.chatHistory = new ChatHistory();
            this.lastSentMessage = null;
            server.registerUser(this);
        }

        public String getName() {
            return name;
        }

        // Method to send a message to one or more other users
        public void sendMessage(List<User> recipients, String content) {

            Message message = new Message(this, recipients, content);
            server.sendMessage(message);
            chatHistory.addMessage(message);
            lastSentMessage = message;
        }

        // Method to undo the last message sent using a Memento
        public void undoLastMessage() {
            if (lastSentMessage != null) {
                MessageMemento memento = lastSentMessage.saveToMemento();
                lastSentMessage.restoreFromMemento(memento);
                chatHistory.removeLastMessage();
                lastSentMessage = null;
            }
        }

        // Method to receive a message from another user
        public void receiveMessage(Message message) {
            chatHistory.addMessage(message);
        }

        // Method to get the chat history for this user
        public List<Message> getChatHistory() {
            return chatHistory.getMessages();
        }

        // Method to block messages from a specific user using the Mediator pattern
        public void blockUser(User user) {
            server.blockMessagesFromUser(this, user);
        }

        // toString method for debugging purposes
        public String toString() {
            return name;
        }
    }

    // ChatServer class representing a mediator between users
    class ChatServer {
        private final List<User> users;
        private final List<Message> messages;
        private final List<User> blockedUsers;

        public ChatServer() {
            this.users = new ArrayList<>();
            this.messages = new ArrayList<>();
            this.blockedUsers = new ArrayList<>();
        }

        // Method to register a new user with the server
        public void registerUser(User user) {
            users.add(user);
        }
        public void unregisterUser(User user) {
            users.remove(user);
        }
        // Method to unregister a user from the server


        // Method to send a message from one user to one or more other users
        public void sendMessage(Message message) {
            if (!blockedUsers.contains(message.getSender())) {
                messages.add(message);
                for (User recipient : message.getRecipients()) {
                    recipient.receiveMessage(message);
                }
            }
        }

        // Method to block messages from a specific user
        public void blockMessagesFromUser(User blocker, User blockedUser) {
            if (users.contains(blocker) && users.contains(blockedUser)) {
                blockedUsers.add(blockedUser);
            }
        }

        // Method to get the list of messages on the server
        public List<Message> getMessages() {
            return messages;
        }

        // toString method for debugging purposes
        public String toString() {
            return "Server with " + users.size() + " users and " + messages.size() + " messages";
        }}

    // ChatHistory class representing the chat history for a user
    class ChatHistory {
        private final List<Message> messages;public ChatHistory() {
            this.messages = new ArrayList<>();
        }

        public void addMessage(Message message) {
            messages.add(message);
        }

        public void removeLastMessage() {
            if (!messages.isEmpty()) {
                messages.remove(messages.size() - 1);
            }
        }

        public List<Message> getMessages() {
            return messages;
        }
    }

    // MessageMemento class representing a snapshot of a message
    class MessageMemento {
        private final String content;
        private final LocalDateTime timestamp;public MessageMemento(String content, LocalDateTime timestamp) {
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    // Driver class to demonstrate the features of the chat application
        public class ChatAppDriver {
        public static void main(String[] args) {
            ChatServer server = new ChatServer();
            System.out.print("Send messages through chat server \n");
            User alice = new User("Alice", server);
            User bob = new User("Bob", server);
            User charlie = new User("Charlie", server);
            alice.sendMessage(List.of(bob), "Hello Bob!\n");
            bob.sendMessage(List.of(alice), "Hi Alice!\n");
            charlie.sendMessage(List.of(alice), "What are you doing\n");

           // System.out.println(alice.getChatHistory());

            // Demonstrate undo last message feature

            alice.sendMessage(List.of(bob), "I just sent this message, but I regret it");
            System.out.println(alice.getChatHistory());
            System.out.print("Undo last message\n");
            alice.undoLastMessage();
            bob.undoLastMessage();

            // Demonstrate block messages from user feature
            bob.blockUser(alice);
            alice.sendMessage(List.of(bob), "This message won't reach Bob, blocked user message");

            //System.out.print("Chat history is ");
            System.out.println("History of alice\n"+alice.getChatHistory());
            System.out.println("History of Bob\n"+bob.getChatHistory());
            System.out.println("History of Charlie\n"+charlie.getChatHistory()); // No messages yet

        }
    }

