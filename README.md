# 🔧 AutoPoster Backend - SmarTec Mx

This service automates the posting of memes and promotional content to the Facebook and Instagram pages of **SmarTec Mx**, a tech-oriented auto repair shop. It is developed in Java with Spring Boot 3 following a clean architecture pattern. It uses the Imgflip API to generate memes, Google TTS API and FFmpeg to generate videos, and the Meta Graph API to publish them automatically several times per week.

---

## 🚀 Features

- Automatic meme generation with custom text
- Automatic video with curious data generation with AI generated text
- Automated posting to Facebook and Instagram
- Token and expiration tracking stored in PostgreSQL
- Scheduled posting via cron jobs
- Complete token lifecycle handled via a single backend endpoint
- App remains in development mode (admin-only use)

---

## 🔐 Tokens Used

| Token Type          | Purpose                              | Estimated Duration        |
| ------------------- | ------------------------------------ | ------------------------- |
| `short_lived_token` | Manually generated                   | ~1–2 hours                |
| `long_lived_token`  | Obtained from the short token        | ~60 days                  |
| `page_token`        | Used to publish directly to the page | Refreshed with long token |

---

## 🔄 Refreshing the Short-Lived Token

This step is done **manually every ~60 days**, but only requires one API call once you have the short token.

### ✅ Prerequisites

- Logged into Facebook with an account that is admin of the page
- Access to [Graph API Explorer](https://developers.facebook.com/tools/explorer)
- App must be in **development mode**

---

### 🧭 Step 1: Generate the short-lived token manually

1. Go to [Graph API Explorer](https://developers.facebook.com/tools/explorer)
2. Select your app (`Bot Automated Poster`)
3. Ensure the following scopes are selected:

```
publish_video
pages_show_list
business_management
instagram_basic
instagram_content_publish
pages_read_engagement
pages_read_user_content
pages_manage_posts
```

4. Click **"Generate Access Token"**
5. Copy the generated token (short-lived)

---

### 🚀 Step 2: Call the backend endpoint

Once you have the short-lived token, make the following request:

```
GET http://localhost:8080/api/app/postingBot/v1/meta/getNewPageLongToken/{short_lived_token}
```

Replace `{short_lived_token}` with the actual token value.

> ✅ This endpoint automatically:
>
> - Exchanges it for a long-lived token
> - Retrieves the page token
> - Calculates estimated expiration
> - Stores everything in PostgreSQL

---

## 🧠 Additional Notes

- `expires_at` may return `0` — this is expected. The backend calculates expiration based on `issued_at + 59 days`
- If the long token becomes invalid or expires, logs will alert you
- The system only works for the registered admin user

---

## � Scheduled Tasks (Cron Schedule)

All times are in **America/Mexico_City** timezone.

| Day                | Time                | Endpoint           | Platform             | Action               |
| ------------------ | ------------------- | ------------------ | -------------------- | -------------------- |
| **Tuesday**        | 12:00 PM            | `postCuriousFact`  | Facebook & Instagram | Post Curious Fact    |
| **Friday**         | 12:30 PM            | `postMeme`         | Instagram            | Post Meme            |
| **Friday**         | 6:00 PM (18:00)     | `postMeme`         | Facebook             | Post Meme            |
| **Sunday**         | 6:00 PM (18:00)     | `postTechnicalTip` | Instagram            | Post Technical Tip   |
| **Sunday**         | 7:00 PM (19:00)     | `postTechnicalTip` | Facebook             | Post Technical Tip   |
| **Every 12 hours** | 12:00 AM & 12:00 PM | `tokenStatus`      | Meta                 | Check Token Validity |

### Cron Expression Reference

```
0 0 12 * * 2       → Tuesday 12:00 PM (Curious Facts)
0 30 12 * * 5      → Friday 12:30 PM (Instagram Meme)
0 0 18 * * 5       → Friday 6:00 PM (Facebook Meme)
0 0 18 * * 7       → Sunday 6:00 PM (Instagram Technical Tip)
0 0 19 * * 7       → Sunday 7:00 PM (Facebook Technical Tip)
0 0 */12 * * *     → Every 12 hours (Token Status Check)
```

---

## �🛠 Technologies

- Java 17
- Spring Boot
- PostgreSQL
- RESTTemplate
- Docker
- FFmpeg
- Imgflip API
- Google TTS API
- Facebook Graph API v23.0

---

## 📌 To-Do

- Creation of technical advice post
- Dockerfile (considering the FFmpeg installation)
- Review version v24.0 from Facebook Graph API to see compatibility

---

## 🤝 External Services

- Gmail
- Imgflip
- Google TTS
- Cloudinary

---

## ✍️ Author

**Jorge Iván Ramos Zebadúa**  
SmarTec Mx · Tuxtla Gutiérrez, Chiapas
