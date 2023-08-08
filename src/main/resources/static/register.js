document.getElementById('register').addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const passwordCheck = document.getElementById('password-check');
    const email = document.getElementById('email');

    //사용자 입력 검증
    if (username.value === '') {
        username.focus();
        alert("사용자 이름을 입력해주세요.");
    } else if (password.value === '') {
        password.focus();
        alert("사용자 비밀번호를 입력해주세요.");
    } else if (passwordCheck === '') {
        passwordCheck.focus();
        alert("비밀번호 확인을 위해 한 번 더 입력해주세요.");
    } else if (password.value !== passwordCheck.value) {
        alert("비밀번호가 일치하지 않습니다.");
    } else if (email.value === '') {
        email.focus();
        alert("이메일을 입력해주세요.");
    }


    await fetch('/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username.value,
            password: password.value,
            email: email.value
        })
    }).catch((error) => {
        console.log("[register] fetch fail");
        console.log(error);
    })


    //로그인 페이지로 리다이렉트
    location.href = 'login.html';
})