package com.gestion_stock_it.ChatBot;

public class Chat {
    private String type;
    private String content;
    public Chat(String type, String content) {
        this.type = type;
        this.content = content;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        if(type.equals("Bot")) {
            this.type = "Bot";
        }
        else if(!type.trim().isEmpty()) {
            this.type = type;
        }
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
