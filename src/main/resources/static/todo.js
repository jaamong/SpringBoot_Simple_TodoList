window.onload = function () {
    const todoListUl = document.getElementById('todo-list-ul');

    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    const userId = localStorage.getItem('userId');

    readAll(); //페이지 첫 로드 시에만 호출, 그 이후로는 싱글 호출

    //로그아웃
    document.getElementById('logout').addEventListener('click', async (event) => {
        event.preventDefault();

        try {
            const promise = await fetch(`/auth/logout/${userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + accessToken,
                    'Authorization-Expiration': 'ExpRTkn' + refreshToken
                }
            })

            if (!promise.ok) {
                console.log("[logout] fetch fail")
            } else {
                //성공적으로 로그아웃이 되면 로그인 페이지로 이동
                alert("정상적으로 로그아웃이 되었습니다.\n확인 버튼을 누르면 로그인 페이지로 이동합니다.");
                location.href = 'login.html';
            }
        } catch (e) {
            console.log(e);
        }
    })

    //새로운 todo 생성
    document.getElementById('create').addEventListener('submit', async (event) => {
        event.preventDefault();

        const todoEl = document.getElementById('todo');
        const todoElValue = todoEl.value;

        if (todoElValue === '')
            alert("You must write something in the box!");

        try {
            const createResponse = await fetch(`/users/${userId}/todos`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + accessToken,
                        'Authorization-Expiration' : 'ExpRTkn' + refreshToken
                    },
                    body: JSON.stringify({
                        content: todoElValue,
                        done: false
                    })
                })
                .catch((error) => {
                    console.log("[create todo] fetch fail");
                    console.log(error)
                })

            const todoDto = await createResponse.json();
            console.log(todoDto);


            // --- tasks ---
            const listEl = document.querySelector("#tasks");

            const taskEl = document.createElement("div");
            taskEl.idx = todoDto.id;
            taskEl.classList.add('row'); //class="row"

            const taskContentEl = document.createElement("div");

            taskEl.appendChild(taskContentEl);

            const taskInputEl = document.createElement("input"); //입력된 내용
            taskInputEl.type = "text";
            taskInputEl.value = todoDto.content;
            taskInputEl.setAttribute("readonly", "readonly");
            taskInputEl.classList.add('form-control');

            taskContentEl.appendChild(taskInputEl);

            const taskActionsEl = document.createElement("div");

            const taskCheckDivEl = document.createElement("div");
            taskCheckDivEl.classList.add('cbDiv')

            const cbId = "cb" + todoDto.id;

            const taskCheckEl = document.createElement("input");
            taskCheckEl.type = "checkbox";
            taskCheckEl.setAttribute("id", cbId);

            const taskCheckLabelEl = document.createElement("label");
            taskCheckLabelEl.setAttribute("for", cbId)

            taskCheckDivEl.appendChild(taskCheckEl);
            taskCheckDivEl.appendChild(taskCheckLabelEl);

            const taskEditEl = document.createElement("button");
            taskEditEl.innerHTML = "EDIT";
            taskEditEl.classList.add('btn1');

            const taskDeleteEl = document.createElement("button");
            taskDeleteEl.innerHTML = "DELETE";
            taskDeleteEl.classList.add('btn2');

            taskActionsEl.appendChild(taskCheckDivEl);
            taskActionsEl.appendChild(taskEditEl);
            taskActionsEl.appendChild(taskDeleteEl);

            taskEl.appendChild(taskActionsEl);

            listEl.appendChild(taskEl);

            const br1 = document.createElement("br");
            const br2 = document.createElement("br");
            const br3 = document.createElement("br");
            listEl.appendChild(br1);
            listEl.appendChild(br2);
            listEl.appendChild(br3);

            todoEl.value = "";


            //할 일 수정
            taskEditEl.addEventListener('click', () => {
                if (taskEditEl.innerHTML == "EDIT") {
                    taskInputEl.removeAttribute("readonly");
                    taskInputEl.focus();

                    taskEditEl.innerText = "SAVE";

                    updateContent(taskInputEl.value, taskEl.idx);
                } else {
                    taskInputEl.setAttribute("readonly", "readonly");
                    taskEditEl.innerText = "EDIT";
                }
            })

            //할 일 삭제
            taskDeleteEl.addEventListener('click', () => {
                if (taskDeleteEl.innerHTML === "DELETE") {
                    listEl.removeChild(taskEl);
                    deleteTodo(taskEl.idx);
                    listEl.removeChild(br1);
                    listEl.removeChild(br2);
                    listEl.removeChild(br3);
                }
            })

            // 할 일 완료/취소
            taskCheckEl.addEventListener('click', () => {
                updateDone(taskEl.idx);
            })
        } catch (e) {
            console.log(e)
        }
    })

    //모든 todo 불러오기
    async function readAll() {
        await fetch(`/users/${userId}/todos`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + accessToken,
                    'Authorization-Expiration': 'ExpRTkn' + refreshToken
                }
            })
            .then(response => {
                console.log(response);
                return response.json()
            })
            .then(json => {
                console.log(json);

                //로그아웃한 사용자가 접근한 경우, 로그인 화면으로 보내기
                if (json.message === "ALREADY_LOGOUT_USER") {
                    alert("다시 로그인한 후 이용해 주세요");
                    location.href = 'login.html';
                } else { //정상 접근
                    console.log(json)
                    for (let key of json) {
                        todoListUl.innerHTML = key.content;
                    }
                }
            })
            .catch(error => {
                console.log(error)
            })
    }

    //todo done update 함수 (fetch)
    async function updateDone(todoId) {

        await fetch(`/users/${userId}/todos/${todoId}/done`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + accessToken,
                    'Authorization-Expiration': 'ExpRTkn' + refreshToken
                }
            })
            .catch((error) => console.log(error.message))
    }

    //todo content update 함수 (fetch)
    async function updateContent(content, todoId) {

        await fetch(`/users/${userId}/todos/${todoId}/content`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + accessToken,
                    'Authorization-Expiration': 'ExpRTkn' + refreshToken
                },
                body: JSON.stringify(content)
            })
            .catch((error) => console.log(error.message))
    }

    //todo 제거 함수 (fetch)
    async function deleteTodo(todoId) {
        await fetch(`/users/${userId}/todos/${todoId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + accessToken,
                    'Authorization-Expiration': 'ExpRTkn' + refreshToken
                }
            })
            .catch((error) => console.log(error.message))
    }
}