<%--
  Created by IntelliJ IDEA.
  User: UTKARSH TIWARI
  Date: 11/5/2022
  Time: 6:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="ISO-8859-1" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spotify Signup</title>
    <meta name="author" content="David Grzyb">
    <meta name="description" content="">

    <!-- Tailwind -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <style>
        @import url('https://fonts.googleapis.com/css?family=Karla:400,700&display=swap');

        .font-family-karla {
            font-family: karla, serif;
        }
    </style>
</head>

<body class="bg-white font-family-karla h-screen">

<div class="min-h-full flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
        <div>
            <img class="mx-auto h-12 w-auto" src="https://tailwindui.com/img/logos/workflow-mark-indigo-600.svg"
                 alt="Workflow">
            <h2 class="mt-6 text-center text-3xl font-extrapolated text-gray-900">Sign in to your account</h2>
            <p class="mt-2 text-center text-sm text-gray-600">
            </p>
        </div>
        <form class="mt-8 space-y-6" action="/signup" method="POST">
            <input type="hidden" name="remember" value="true">
            <div class="rounded-md shadow-sm -space-y-px flex flex-col gap-2">
                <div>
                    <label for="username" class="sr-only">Name</label>
                    <input id="username" name="username" type="text" autocomplete="text" required
                           class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                           placeholder="Username">
                </div>
                <div>
                    <label for="email" class="sr-only">Email address</label>
                    <input id="email" name="email" type="email" autocomplete="email" required
                           class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                           placeholder="Email address">
                </div>
                <div>
                    <label for="password" class="sr-only">Confirm Password</label>
                    <input id="password" name="password" type="password"
                           autocomplete="current-password" required
                           class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                           placeholder="Password">
                </div>
                <div>
                    <label for="confirm-password" class="sr-only">Password</label>
                    <input id="confirm-password" name="password" type="password"
                           autocomplete="current-password" required
                           class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                           placeholder="Confirm Password">
                </div>
            </div>

            <div class="flex items-center justify-between">
                <div class="flex items-center">

                </div>

                <div class="text-sm">
                    <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500">forget password</a>
                </div>
            </div>

            <div>
                <button type="submit"
                        class="group relative bg-gradient-to-r from-purple-500 via-purple-600 to-purple-700 hover:bg-gradient-to-br focus:ring-4 focus:outline-none focus:ring-purple-300 dark:focus:ring-purple-800 w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white  ">
                        <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                            <svg class="h-5 w-5 text-indigo-500 group-hover:text-indigo-400"
                                 xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"
                                 aria-hidden="true">
                                <path fill-rule="evenodd"
                                      d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                                      clip-rule="evenodd"></path>
                            </svg>
                        </span>
                    Sign in
                </button>
            </div>
        </form>
    </div>
</div>
<script>
    const form = document.querySelector('form');
    const emailElement=document.getElementById("email");
    const usernameElement = document.getElementById('username');
    const passwordElement = document.getElementById('password');
    const confirm_passwordElement = document.getElementById('confirm-password');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email=emailElement.value;
        const password = passwordElement.value;
        const username = usernameElement.value;
        const confirm_password = confirm_passwordElement.value;
        try {
            if (password !== confirm_password||(!username)) {
                location.assign('/signup');
                email.value = '';
                password.value = '';
            } else {
                const res = await fetch('/signup', { method: 'POST', body: JSON.stringify({ username, password , email}), headers: { 'Content-Type': 'application/json' } })
                if(res.status===200)
                    location.assign("/login")
            }
        } catch (err) {
            console.log(err);
        }
    });
</script>
</body>

</html>