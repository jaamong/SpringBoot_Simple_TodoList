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

        const todoDto = await createResponse.json();
        console.log(todoDto.id);

        if (!createResponse.ok) {
            alert(todoDto.message)
            return
        }

        // --- tasks ---
        const listEl = document.querySelector("#tasks");

        const taskEl = document.createElement("div");
        taskEl.idx = todoDto.id;

        const taskContentEl = document.createElement("div");

        taskEl.appendChild(taskContentEl);

        const taskInputEl = document.createElement("input");
        taskInputEl.type = "text";
        taskInputEl.value = todoDto.content;
        taskInputEl.setAttribute("readonly", "readonly");

        taskContentEl.appendChild(taskInputEl);

        const taskActionsEl = document.createElement("div");

        const taskCheckEl = document.createElement("input");
        taskCheckEl.type = "checkbox";

        const taskEditEl = document.createElement("button");
        taskEditEl.innerHTML = "EDIT";

        const taskDeleteEl = document.createElement("button");
        taskDeleteEl.innerHTML = "DELETE";

        taskActionsEl.appendChild(taskCheckEl);
        taskActionsEl.appendChild(taskEditEl);
        taskActionsEl.appendChild(taskDeleteEl);

        taskEl.appendChild(taskActionsEl);

        listEl.appendChild(taskEl);

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
            }
        })

        // 할 일 완료/취소
        taskCheckEl.addEventListener('click', () => {
            taskCheckEl.classList.toggle("checked");
            updateDone(taskEl.idx);
        })
    } catch (error) {
        console.log(error.message)
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
    try {
        await fetch(`/users/${userId}/todos/${todoId}/done`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        })
    } catch (error) {
        console.log(error.message)
    }
}

//todo content update 함수 (fetch)
async function updateContent(content, todoId) {
    try {
        await fetch(`/users/${userId}/todos/${todoId}/content`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(content)
        })
    } catch (error) {
        console.log(error.message);
    }

}

//todo 제거 함수 (fetch)
async function deleteTodo(todoId) {
    try {
        await fetch(`/users/${userId}/todos/${todoId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        })
    } catch (error) {
        console.log(error.message);
    }
}