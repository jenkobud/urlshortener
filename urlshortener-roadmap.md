# URL Shortener API Roadmap

Create a simple RESTful API that allows users to shorten long URLs. The API must support CRUD operations for short URLs and provide access statistics.

![URL Shortening Service](https://assets.roadmap.sh/guest/url-shortener-architecture-u72mu.png)

## Requirements

Build a RESTful API for a URL shortening service with the following operations:

- Create a new short URL
- Retrieve an original URL from a short URL
- Update an existing short URL
- Delete an existing short URL
- Get statistics for a short URL (e.g., number of times accessed)

Optional:

- Build a minimal frontend to interact with the API
- Support redirects from short URLs to original URLs

## API Endpoints

### 1. Create Short URL

Create a new short URL.

```http
POST /shorten
Content-Type: application/json

{
  "url": "https://www.example.com/some/long/url"
}
```

Expected behavior:

- Validate request body
- Generate a unique, random `shortCode`
- Return `201 Created` with the new record
- Return `400 Bad Request` for validation errors

Example response (`201 Created`):

```json
{
  "id": "1",
  "url": "https://www.example.com/some/long/url",
  "shortCode": "abc123",
  "createdAt": "2021-09-01T12:00:00Z",
  "updatedAt": "2021-09-01T12:00:00Z"
}
```

### 2. Retrieve Original URL

Retrieve an existing short URL record by shortcode.

```http
GET /shorten/abc123
```

Expected behavior:

- Return `200 OK` with the URL record
- Return `404 Not Found` if shortcode does not exist

Example response (`200 OK`):

```json
{
  "id": "1",
  "url": "https://www.example.com/some/long/url",
  "shortCode": "abc123",
  "createdAt": "2021-09-01T12:00:00Z",
  "updatedAt": "2021-09-01T12:00:00Z"
}
```

Note: If you build a frontend redirect flow, the frontend can resolve the short URL and then redirect users to the original URL.

### 3. Update Short URL

Update the destination URL for an existing shortcode.

```http
PUT /shorten/abc123
Content-Type: application/json

{
  "url": "https://www.example.com/some/updated/url"
}
```

Expected behavior:

- Validate request body
- Return `200 OK` with the updated record
- Return `400 Bad Request` for validation errors
- Return `404 Not Found` if shortcode does not exist

Example response (`200 OK`):

```json
{
  "id": "1",
  "url": "https://www.example.com/some/updated/url",
  "shortCode": "abc123",
  "createdAt": "2021-09-01T12:00:00Z",
  "updatedAt": "2021-09-01T12:30:00Z"
}
```

### 4. Delete Short URL

Delete an existing short URL.

```http
DELETE /shorten/abc123
```

Expected behavior:

- Return `204 No Content` on successful deletion
- Return `404 Not Found` if shortcode does not exist

### 5. Get URL Statistics

Retrieve statistics for a short URL.

```http
GET /shorten/abc123/stats
```

Expected behavior:

- Return `200 OK` with URL metadata and `accessCount`
- Return `404 Not Found` if shortcode does not exist

Example response (`200 OK`):

```json
{
  "id": "1",
  "url": "https://www.example.com/some/long/url",
  "shortCode": "abc123",
  "createdAt": "2021-09-01T12:00:00Z",
  "updatedAt": "2021-09-01T12:00:00Z",
  "accessCount": 10
}
```

## Suggested Tech Stack

You can use any language, framework, and database.

Suggested backend options:

- JavaScript: Node.js + Express.js
- Python: Flask or Django
- Java: Spring Boot
- Ruby: Ruby on Rails

Suggested database options:

- SQL: MySQL
- NoSQL: MongoDB

## Scope

Focus on core API functionality:

- Create, retrieve, update, and delete short URLs
- Track and return access statistics

Authentication and authorization are out of scope for this project.