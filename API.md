# REST API and Angular 17 integration

This guide documents the Spring Boot API currently in this project and shows how to call it from an Angular 17 client.

## Base URL and HTTP conventions

- Local API base URL: `http://localhost:8080`
- API paths start with `/api` except the test email route `/test/sendMail`.
- Send JSON with `Content-Type: application/json` for JSON request bodies.
- JWT protected requests use `Authorization: Bearer <jwtToken>`.
- The backend returns JSON DTOs for user, product, and dashboard data. Passwords are not part of response DTOs.
- Times are serialized as ISO 8601 strings, for example `2026-10-01T10:30:00+06:00`.
- Product `price` is a JSON number. Use a TypeScript `number` for it.

The CORS configuration allows the origins in `CORS_ALLOWED_ORIGINS`, defaulting to `http://localhost:4200`. It allows `Authorization` and JSON headers and does not use cookie credentials. For local development, either call `http://localhost:8080` directly or configure the Angular dev-server proxy described below.

## Authorization model

Roles are fixed enum values. A new account receives only `ROLE_USER`. An administrator assigns additional roles; registration never accepts a role from the client.

| Role | Main access |
|---|---|
| `ROLE_USER` | User dashboard, own profile, authenticated product detail |
| `ROLE_MODERATOR` | User access plus moderator dashboard and product create/update |
| `ROLE_ADMIN` | User access plus admin dashboard, user/role management, and product delete |
| `ROLE_PREMIUM_USER` | User access plus premium dashboard and premium product list |
| `ROLE_EMPLOYEE` | User dashboard |

The server enforces access. Angular guards and hidden menu items improve navigation only; they do not replace the server's role checks.

## Endpoints

### Authentication

| Method | Path | Access | Success |
|---|---|---|---|
| `POST` | `/api/auth/signup` | Public | `200`, plain text success message |
| `POST` | `/api/auth/signin` | Public | `200`, JWT and user DTO |

Signup request:

```json
{
  "username": "alex",
  "password": "a-strong-password",
  "email": "alex@example.com",
  "firstName": "Alex",
  "lastName": "Morgan"
}
```

`username` is required (max 100 characters); `password` is required (8–72 characters); `email` is required and must be valid (max 255 characters). Names are optional (max 255 characters). Signup returns the text `User registered successfully`; it does not log the new account in automatically.

Login request:

```json
{
  "username": "alex",
  "password": "a-strong-password"
}
```

Login response:

```json
{
  "jwtToken": "eyJ...",
  "user": {
    "userName": "alex",
    "userFirstName": "Alex",
    "userLastName": "Morgan",
    "email": "alex@example.com",
    "enabled": true,
    "roles": ["ROLE_USER"],
    "dateCreated": "2026-10-01T10:30:00+06:00"
  }
}
```

Use the `jwtToken` property as the bearer token. A duplicate username or email on signup returns `400` with a text message. Invalid login credentials return `401` with `Invalid credentials`.

### Products

| Method | Path | Access | Success |
|---|---|---|---|
| `GET` | `/api/products` | Public | `200`, active product DTO array |
| `GET` | `/api/products/{id}` | Any authenticated user | `200`, product DTO; `404` if missing/inactive |
| `GET` | `/api/products/premium` | `ROLE_PREMIUM_USER` | `200`, premium product DTO array |
| `POST` | `/api/products` | `ROLE_MODERATOR` or `ROLE_ADMIN` | `201`, created DTO and `Location` header |
| `PUT` | `/api/products/{id}` | `ROLE_MODERATOR` or `ROLE_ADMIN` | `200`, updated DTO; `404` if missing |
| `DELETE` | `/api/products/{id}` | `ROLE_ADMIN` | `204`; `404` if missing |

Create/update body:

```json
{
  "name": "Wireless Mouse",
  "category": "Accessories",
  "price": 24.99,
  "description": "Wireless optical mouse",
  "stock": 25,
  "active": true,
  "premium": false,
  "imageUrl": "https://example.com/mouse.jpg"
}
```

Required values: `name` (nonblank, max 150), `category` (nonblank, max 100), `price` (at least `0.01`), and `stock` (zero or more). `description` and `imageUrl` may be omitted; `active` defaults to `true` and `premium` defaults to `false` when creating. `id` and `createdAt` are response fields and are ignored when saving a request.

Product response shape:

```json
{
  "id": 1,
  "name": "Wireless Mouse",
  "category": "Accessories",
  "price": 24.99,
  "description": "Wireless optical mouse",
  "stock": 25,
  "active": true,
  "premium": false,
  "imageUrl": "https://example.com/mouse.jpg",
  "createdAt": "2026-10-01T10:30:00+06:00"
}
```

On an empty product table the application inserts three demo products, including one premium product.

### User profile and administration

| Method | Path | Access | Success |
|---|---|---|---|
| `GET` | `/api/users` | `ROLE_ADMIN` | `200`, user DTO array |
| `GET` | `/api/users/{username}` | Same user or `ROLE_ADMIN` | `200`, user DTO; `404` if missing |
| `PUT` | `/api/users/{username}` | Same user or `ROLE_ADMIN` | `200`, updated user DTO; `404` if missing |
| `DELETE` | `/api/users/{username}` | `ROLE_ADMIN` | `200`, plain text message; `404` if missing |
| `PATCH` | `/api/users/{username}/status` | `ROLE_ADMIN` | `200`, updated user DTO; `404` if missing |
| `GET` | `/api/admin/roles` | `ROLE_ADMIN` | `200`, fixed role-name array |
| `PUT` | `/api/admin/users/{username}/roles` | `ROLE_ADMIN` | `200`, updated user DTO; `404` if missing |
| `GET` | `/api/admin/roles/{roleName}/users` | `ROLE_ADMIN` | `200`, users with that role; `404` for an invalid role |
| `GET` | `/api/admin/statistics` | `ROLE_ADMIN` | `200`, admin statistics DTO |

Profile update body (all fields optional):

```json
{
  "firstName": "Alexandra",
  "lastName": "Morgan",
  "email": "alexandra@example.com"
}
```

Status update body:

```json
{"enabled": false}
```

Role update replaces the user's complete role set, so include every role the account should retain:

```json
{"roles": ["ROLE_USER", "ROLE_MODERATOR"]}
```

Supported role names are `ROLE_USER`, `ROLE_MODERATOR`, `ROLE_ADMIN`, `ROLE_EMPLOYEE`, and `ROLE_PREMIUM_USER`. The role list endpoint returns these names as JSON strings. No endpoint creates a new role.

User DTO response shape:

```json
{
  "userName": "alex",
  "userFirstName": "Alex",
  "userLastName": "Morgan",
  "email": "alex@example.com",
  "enabled": true,
  "roles": ["ROLE_USER"],
  "dateCreated": "2026-10-01T10:30:00+06:00"
}
```

Some admin user DTO responses omit `dateCreated` (or serialize it as `null`). Never expect or send a password through these endpoints.

### Dashboards

All dashboard endpoints require a bearer token.

| Method | Path | Required role | Data |
|---|---|---|---|
| `GET` | `/api/dashboard/user` | Any defined role | User name, roles, active/premium product counts, up to 3 active products |
| `GET` | `/api/dashboard/moderator` | `ROLE_MODERATOR` | Total/active/inactive/low-stock product counts, up to 5 active products |
| `GET` | `/api/dashboard/admin` | `ROLE_ADMIN` | User and product totals, up to 5 active products |
| `GET` | `/api/dashboard/premium` | `ROLE_PREMIUM_USER` | Premium product count and up to 5 active premium products |

Dashboard response shape:

```json
{
  "dashboard": "moderator",
  "username": "alex",
  "roles": ["ROLE_MODERATOR"],
  "metrics": {
    "totalProducts": 3,
    "activeProducts": 3,
    "inactiveProducts": 0,
    "lowStockProducts": 0
  },
  "products": []
}
```

`metrics` is a map; its keys differ by dashboard as listed above. A caller with a role that does not match a specialist dashboard receives `403 Forbidden`.

### Security/demo endpoints

| Method | Path | Access | Result |
|---|---|---|---|
| `GET` | `/api/test/all` | `ROLE_USER`, `ROLE_MODERATOR`, or `ROLE_ADMIN` | `{ "message": "User content." }` |
| `GET` | `/api/test/mod` | `ROLE_MODERATOR` | Moderator message |
| `GET` | `/api/test/admin` | `ROLE_ADMIN` | Admin message |
| `GET` | `/api/test2/all` | `ROLE_USER`, `ROLE_MODERATOR`, or `ROLE_ADMIN` | User message |
| `GET` | `/api/test2/mod` | `ROLE_MODERATOR` | Moderator message |
| `GET` | `/api/test2/admin` | `ROLE_ADMIN` | Admin message |
| `GET` | `/api/debug/my-roles` | Any authenticated user | Current username and authorities |
| `GET` | `/test/sendMail` | Any authenticated user | Queues 50 demo report emails; avoid calling in production |

OpenAPI UI is available at `/swagger-ui/index.html`; the document is at `/v3/api-docs`.

## Error handling

- `401 Unauthorized`: no valid authentication for a protected endpoint. The security handler returns JSON fields `status`, `timestamp`, `error`, `message`, `path`, and `method`.
- `403 Forbidden`: authenticated but the account lacks the required role. The response contains `status`, `error`, `message`, and `path`.
- `400 Bad Request`: invalid DTO fields, duplicate signup username/email, or an invalid role update body.
- `404 Not Found`: requested product/user is absent or inactive where applicable.
- Sign-up and user deletion success bodies are plain text. Product deletion returns `204` with no body.

Validation error JSON is generated by Spring MVC and may differ from the custom security error shape.

## Angular 17 setup

Angular `HttpClient` returns RxJS observables. Provide it at app startup and register a functional interceptor; keep HTTP calls in injectable services rather than components. See the [Angular HTTP client guide](https://v17.angular.io/guide/http) and [Angular development-server proxy guide](https://v17.angular.io/guide/build#proxying-to-a-backend-server).

### Environment URL

`src/environments/environment.ts`:

```ts
export const environment = {
  apiBaseUrl: 'http://localhost:8080',
};
```

For a production build, use the deployed HTTPS API origin in the production environment file.

### DTO types

```ts
export type Role =
  | 'ROLE_USER'
  | 'ROLE_MODERATOR'
  | 'ROLE_ADMIN'
  | 'ROLE_EMPLOYEE'
  | 'ROLE_PREMIUM_USER';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  firstName?: string;
  lastName?: string;
}

export interface UserDto {
  userName: string;
  userFirstName: string | null;
  userLastName: string | null;
  email: string;
  enabled: boolean;
  roles: Role[];
  dateCreated?: string | null;
}

export interface LoginResponse {
  jwtToken: string;
  user: UserDto;
}

export interface ProductDto {
  id?: number;
  name: string;
  category: string;
  price: number;
  description?: string | null;
  stock: number;
  active?: boolean;
  premium?: boolean;
  imageUrl?: string | null;
  createdAt?: string;
}

export interface DashboardDto {
  dashboard: 'user' | 'moderator' | 'admin' | 'premium';
  username: string;
  roles: Role[];
  metrics: Record<string, number>;
  products: ProductDto[];
}
```

### Register `HttpClient` and the bearer interceptor

`src/app/auth.interceptor.ts`:

```ts
import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const isApiRequest = req.url.startsWith(environment.apiBaseUrl);
  const token = sessionStorage.getItem('jwtToken');

  if (!isApiRequest || !token) {
    return next(req);
  }

  return next(req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  }));
};
```

`src/app/app.config.ts` for a standalone Angular 17 application:

```ts
import { ApplicationConfig } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [provideHttpClient(withInterceptors([authInterceptor]))],
};
```

If the app uses NgModules, register `provideHttpClient(withInterceptors([authInterceptor]))` in the bootstrap/application providers. No `withCredentials` option is needed; this API returns a bearer token and does not authenticate with cookies.

### Authentication service

```ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiBaseUrl}/api/auth`;

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.url}/signin`, request).pipe(
      tap(response => sessionStorage.setItem('jwtToken', response.jwtToken)),
    );
  }

  register(request: RegisterRequest): Observable<string> {
    return this.http.post(`${this.url}/signup`, request, { responseType: 'text' });
  }

  logout(): void {
    sessionStorage.removeItem('jwtToken');
  }
}
```

After successful signup, show the success message and ask the user to sign in. After login, use `response.user.roles` to decide which dashboard link to display. Clear the token on logout. For a production client, choose a token storage strategy that fits your threat model; the server remains responsible for all authorization decisions.

### Product service

```ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductApiService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiBaseUrl}/api/products`;

  list(): Observable<ProductDto[]> {
    return this.http.get<ProductDto[]>(this.url);
  }

  get(id: number): Observable<ProductDto> {
    return this.http.get<ProductDto>(`${this.url}/${id}`);
  }

  getPremium(): Observable<ProductDto[]> {
    return this.http.get<ProductDto[]>(`${this.url}/premium`);
  }

  create(product: ProductDto): Observable<ProductDto> {
    return this.http.post<ProductDto>(this.url, product);
  }

  update(id: number, product: ProductDto): Observable<ProductDto> {
    return this.http.put<ProductDto>(`${this.url}/${id}`, product);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
```

### Dashboards and role-aware navigation

```ts
getDashboard(kind: DashboardDto['dashboard']): Observable<DashboardDto> {
  return this.http.get<DashboardDto>(`${environment.apiBaseUrl}/api/dashboard/${kind}`);
}
```

Choose the dashboard URL based on the signed-in user's roles. For users who have multiple roles, a practical client priority is admin, moderator, premium, then user. A role route guard can prevent confusing navigation, but the API still returns `403` if a caller requests another role's dashboard.

### Signup and user-delete text responses

Spring returns these two success bodies as plain text. Set the Angular `responseType` so `HttpClient` does not try to parse the text as JSON:

```ts
register(body: RegisterRequest) {
  return this.http.post(`${environment.apiBaseUrl}/api/auth/signup`, body, {
    responseType: 'text',
  });
}

deleteUser(username: string) {
  const path = encodeURIComponent(username);
  return this.http.delete(`${environment.apiBaseUrl}/api/users/${path}`, {
    responseType: 'text',
  });
}
```

### Local Angular proxy (optional)

To call the backend with relative `/api/...` URLs during `ng serve`, configure an Angular dev-server proxy targeting `http://localhost:8080` and set the Angular API base URL to an empty string. This avoids browser cross-origin requests in local development. Angular documents the proxy setup [here](https://v17.angular.io/guide/build#proxying-to-a-backend-server). If you use relative URLs, adjust the interceptor's `isApiRequest` check to recognize only your `/api/` paths.

## Suggested integration order

1. Call `POST /api/auth/signup` and then sign in with `POST /api/auth/signin`.
2. Save `jwtToken` and attach it to protected API requests with the interceptor.
3. Call `GET /api/products` to render the public catalog.
4. Use the logged-in user's roles to show suitable dashboard and product-management navigation.
5. Call the relevant `/api/dashboard/{role}` endpoint and handle `401` by returning to login and `403` by showing an access-denied view.
6. Use an administrator account to assign `ROLE_MODERATOR` or `ROLE_PREMIUM_USER` to test specialized dashboards and actions.
