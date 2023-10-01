package com.example.bookshop.sessions;

public interface SessionsManager {
     void deleteSessionExceptCurrentByUser(String username);
}
