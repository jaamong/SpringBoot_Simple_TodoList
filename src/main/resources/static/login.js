document.getElementById('login').addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const LoginResponse = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        })

        const data = await LoginResponse.json();

        if(!LoginResponse.ok) {
            console.log("[login] fetch fail");
            throw new Error(data.message);
        }

        console.log(data);

        localStorage.setItem('token', data.token) //로컬 스토리지에 token 설정
        localStorage.setItem('userId', data.user.id)

        location.href = 'todo.html';
    } catch (e) {
        console.log(e);
    }
})

//회원 가입 페이지로 이동
document.getElementById('register').addEventListener('click', () => {
    location.href = 'register.html';
})