Posts App - A Spring Boot Application
===
* The Posts App is a multi-module Spring Boot application that provides a secure and flexible system for user authentication, post management, and administrative functionalities. It is composed of three main modules: Auth, Posts, and Admin.

## Auth Module:

* Sign up and Sign in Endpoints: This module allows users to register new accounts and authenticate themselves securely using credentials.

## Posts Module:

* Create, Update, and Delete Posts: Authenticated users can create, update, and delete their posts.
* Fetch User Posts: Users can retrieve their posts, including both approved and unapproved ones.
* Fetch Approved Posts: Authenticated and non-authenticated users can view all approved posts or retrieve an approved post by its ID.
* Comments: Users can add comments to posts, fostering community interaction.
* Post Analytics: Tracks post views and likes, allowing users to monitor post engagement. Each user can only like a post once.
* Likes: Provides a like system where users can express their approval of posts. The system ensures that each user can like a post only once, and they also have the option to remove their like (unlike) if desired.
* Viewing Count: An aspect is used to implement and track the number of views per post.

## Admin Module:

* Manage User Roles: Admins can assign roles to users, such as USER, ADMIN, or SUPER_ADMIN.
* Activate User Accounts: Admins can activate users’ accounts.
* Approve Posts: Admins have the authority to approve posts before they are publicly visible.
* Fetch User Information: Retrieve detailed information for all users or specific users by their ID or email.
* Fetch Posts: Admins can view all posts, whether approved or unapproved, and also retrieve posts for a specific user by ID.
* Login Counter Information: Track login activities for any specific date, with details on successful and failed login attempts.
* Allow admin to delete any comments added by users.

## Login Counter Aspect:

* An Aspect-Oriented Programming (AOP) approach is used to track and log login attempts. This includes a database record of the number of successful, failed, and total login attempts for any given date.
