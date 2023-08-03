const todoListUl = document.getElementById('todo-list-ul');

const token = localStorage.getItem('token');
const userId = localStorage.getItem('userId');
let todoId = 0;

readAll(); //페이지 첫 로드 시에만 호출, 그 이후로는 싱글 호출

//새로운 todo 생성
document.getElementById('create').addEventListener('submit', async (event) => {
    event.preventDefault();

    const newTodo = document.getElementById('todo').value;
    if (newTodo === '') alert("You must write something in the box!");

    console.log(newTodo)

    try {
        const createResponse = await fetch(`/users/${userId}/todos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                content: newTodo,
                done: false
            })
        })
        const todoDto = await createResponse.json();

        if (!createResponse.ok) {
            const ReadAllResponseBody = await createResponse.json();
            alert(ReadAllResponseBody.message)
            return
        }

        todoId = todoDto.id;
        newTodo.value = "";

        //생성하고 ul 태그에 넣기
        let li = document.createElement('li');
        li.innerHTML = todoDto.content;

        let span = document.createElement('span');
        span.innerHTML = "x";
        li.appendChild(span);

        let button = document.createElement('button');
        button.innerHTML = "EDIT";
        button.setAttribute("class", "edit");
        li.appendChild(button);

        todoListUl.appendChild(li);

        //할 일 수정
        button.addEventListener('click', () => {
            if (button.innerHTML == "EDIT") {
                newTodo.removeAttribute("readonly");
                newTodo.focus();
                button.innerText = "SAVE";
                updateContent(newTodo);
            } else {
                newTodo.setAttribute("readonly", "readonly");
                button.innerText = "EDIT";
            }
        })

        //할 일 삭제
        span.addEventListener('click', () => {
            if (event.target.tagName === "SPAN") {
                event.target.parentElement.remove();

                console.log(userId);
                console.log(todoId);

                deleteTodo();
            }
        })

        // 할 일 완료/취소
        li.addEventListener('click', () => {
            if (event.target.tagName === "LI") {
                event.target.classList.toggle("checked");

                console.log(userId);
                console.log(todoId);

                updateDone();
            }
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
async function updateDone() {
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
async function updateContent(content) {
    try {
        await fetch(`/users/${userId}/todos/${todoId}/done`, {
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
async function deleteTodo() {
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