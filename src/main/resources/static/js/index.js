const modal = document.querySelector('#modal');
const deleteBtn = document.querySelectorAll('.deleteBtn');
const modalBtn = document.querySelector('.modal_btn');
const deleteForm = document.querySelector('#delete_form');

modal.addEventListener('click', (event) => {
	const target = event.target;
	if (target === modal || target === modalBtn || target.closest('.modal_close')) {
		modal.style.display = 'none';
		deleteForm.action = '';
	}
});

for (let i = 0; i < deleteBtn.length; i++) {
	deleteBtn[i].addEventListener('click', (event) => {
		modal.style.display = 'flex';
		const id = event.target.name;
		if (id) {
			deleteForm.action = `/admin/${event.target.name}/delete`;
		} else {
			deleteForm.action = `/user/delete`;
		}

	});
};

// TODO disable scroll when show modal