# Feature 2: Eco-Encyclopedia with AI Chatbot

Implemented by Kok Wei Huang (2204331). Entry point: `EncyclopediaHomeActivity`,
launched from the app's home screen (`MainActivity`) via the "5R Education Space" button.

## What it does

- Browse/search a local knowledge base of sustainability articles (Room database).
- "Popular Articles" is ranked in real time by how often each article is opened
  (`viewCount`), and can be refreshed with fresh AI-generated content via the
  refresh button (`ArticleContentGenerator`, calls Gemini).
- "Ask AI Assistant" (`ChatActivity`) answers free-form questions using Google's
  Gemini API directly (`network/` package), falling back to simple local keyword
  matching if there's no API key or no internet connection.

## Setup

Add your own Gemini key to `local.properties` at the repo root (see
`local.properties.example`):

```
GEMINI_API_KEY=your_actual_key_here
```

Get a free key from https://aistudio.google.com/apikey. Without a key the module
still works, just using the offline knowledge-base fallback instead of real AI answers.

## Package layout

```
com.example.assignmentgroup.encyclopedia/
├── EncyclopediaHomeActivity.java   Home: search, categories, popular articles, Ask AI entry
├── ArticleDetailActivity.java      Full article + bookmark toggle (increments view count)
├── ChatActivity.java               Ask AI Assistant chat screen
├── model/                          Article, ChatMessage, GeminiRequest, GeminiResponse
├── db/                             Room DAOs + AppDatabase
├── network/                        GeminiApiService, RetrofitClient, ChatRepository, ArticleContentGenerator
├── adapter/                        RecyclerView adapters
└── util/ArticleSeeder.java         Initial demo knowledge base (used before first AI refresh)
```

Model note: the Gemini API model name changes over time as Google retires old ones
(see `network/GeminiApiService.java` for the current model + a link to check the
latest list if the chatbot ever silently reverts to local-only answers).
