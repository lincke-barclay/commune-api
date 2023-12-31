## Local Development

### Environment Variables
The following environment variables are used throughout the application

POSTGRES_DB_HOST=localhost
POSTGRES_DB_NAME=commune
POSTGRES_PASSWORD=abc123
POSTGRES_USERNAME=postgres
LINODE_S3_ACCESS_KEY=...
LINODE_S3_SECRET_KEY=...
TEST1_EMAIL=...
TEST1_PASSWORD=...

## Insomnia Setup

1. Import ./api-reference/insomnia.json into your insomnia project
2. Create a private sub environment that looks like this:
```
{
	"email1": "email of user 1",
	"password1": "password of user 1",
	"email2": "email of user 2",
	"password2": "password of user 2"
}
```
3. Run the commands:
	- Note that because a user isn't created in the database until an authenticated request is made by that user, you'll need to make a request 
	from authenticated user 1 and 2 before you can get the public event / user - otherwise it will be a 404.
	- Note - you'll have to POST an oauth token first before running any request
