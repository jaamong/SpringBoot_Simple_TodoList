document.getElementById('login').addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const promise = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        })

        response = await promise.json();
        console.log(response);

        if (!promise.ok) {
            console.log("[login] fetch fail");

            const message = response.message;

            if (message === "NOT_FOUND_USER" || message === "\"INVALID_PASSWORD\"")
                alert("아이디 또는 비밀번호를 잘못 입력했습니다.");

        } else {
            localStorage.setItem('token', response.token) //로컬 스토리지에 token 설정
            localStorage.setItem('userId', response.user.id)

            location.href = 'todo.html';
        }
    } catch (e) {
        console.log(e);
    }
})

//회원 가입 페이지로 이동
document.getElementById('register').addEventListener('click', () => {
    location.href = 'register.html';
})