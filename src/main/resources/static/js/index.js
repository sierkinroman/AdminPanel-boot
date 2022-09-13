const modal = document.querySelector('#modal');
const deleteBtn = document.querySelectorAll('.deleteBtn');
const modalBtn = document.querySelector('.modal_btn');
const deleteForm = document.querySelector('#delete_form');
const sortLinks = document.querySelectorAll('.table_header a');

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
}

document.addEventListener('DOMContentLoaded', () => {
    const url = new URL(window.location.href);
    for (let i = 0; i < sortLinks.length; i++) {
        if (url.searchParams.get('sortField') === sortLinks[i].dataset.name) {
            if (url.searchParams.get('sortAsc') === 'true') {
                sortLinks[i].classList.add('sort_asc');
            } else if (url.searchParams.get('sortAsc') === 'false') {
                sortLinks[i].classList.add('sort_desc');
            }
        }
    }
});

for (let i = 0; i < sortLinks.length; i++) {
    sortLinks[i].addEventListener('mouseover', () => {
        showSortDirection(sortLinks[i]);
    });

    sortLinks[i].addEventListener('mouseout', () => {
        showSortDirection(sortLinks[i]);
    });
}

function showSortDirection(sortLink) {
    if (sortLink.classList.contains('sort_asc')) {
        swapClasses(sortLink, 'sort_asc', 'sort_desc');
    } else if (sortLink.classList.contains('sort_desc')) {
        swapClasses(sortLink, 'sort_desc', 'sort_asc');
    }
}

function swapClasses(element, removeClass, addClass) {
    element.classList.remove(removeClass);
    element.classList.add(addClass);
}

// TODO disable scroll when show modal