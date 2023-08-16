window.onload = function () {
    const todoListUl = document.getElementById('todo-list-ul');

    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');

    readAll(); //페이지 첫 로드 시에만 호출, 그 이후로는 싱글 호출

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
                        'Authorization': 'Bearer ' + token
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
        const promise = await fetch(`/users/${userId}/todos`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        })

        const contentList = await promise.json();
        console.log(contentList);

        for (let key of contentList) {
            todoListUl.innerHTML = key.content;
        }
    }

    //todo done update 함수 (fetch)
    async function updateDone(todoId) {

        await fetch(`/users/${userId}/todos/${todoId}/done`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
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
                    'Authorization': 'Bearer ' + token
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
                    'Authorization': 'Bearer ' + token
                }
            })
            .catch((error) => console.log(error.message))
    }
}