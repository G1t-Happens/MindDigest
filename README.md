# 🧠 MindDigest

**AI-powered multilingual news summarizer and archiver**  
MindDigest is a web application that automatically crawls research and news websites, summarizes the content using a 
**Large Language Model (LLM)**, categorizes it (e.g., Tech, Health, Politics), and translates articles into English (if
needed). The processed articles are then stored in a database and can be filtered and browsed through a modern frontend
interface.

---

## ✨ Features

- 🌐 **Automatic Web Crawling** of curated news, research, and tech sites
- 🤖 **LLM-based Summarization** (OpenAI)
- 🏷️ **Topic Classification**: Technology, Health, Politics, Science, etc.
- 🌍 **Multilingual Translation** to English (for non-English sources)
- 🔎 **Search, Filter, and Sort** through a clean and responsive frontend
- 🗃️ **Persistent Storage** of summarized articles
- 📆 Sort by **date, relevance**, or **category**

---

## 💻 Tech Stack

| Layer    | Technology                 |
|----------|----------------------------|
| Frontend | Vue 3, Pinia, Tailwind CSS |
| Backend  | Java 21, Spring Boot       |
| Crawling | WebMagic                   |
| LLM      | OpenAI                     |
| Database | PostgreSQL                 |

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- PostgreSQL

### Backend

1. Start your database
2. Navigate to backend
    ```shell
    cd backend
    ```

3. Add your own `.env` file in the backend root folder with your database credentials following the schema below:

    ```env
    DB_URL=jdbc:<database_type>://<host>:<port>/<database_name>
    DB_USER=<user>
    DB_PASSWORD=<password>
    ```

4. Start the backend:
    ```shell
    gradle bootRun
    ```

### Frontend

1. Navigate to frontend, install dependencies, and start the frontend:
    ```shell
    cd frontend
    npm install
    npm run serve
    ```





