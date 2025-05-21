document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/blog/posts')
        .then(response => response.json())
        .then(data => {
            const blogContainer = document.querySelector(".container");
            data.forEach(post => {
                const postElement = document.createElement("div");
                postElement.classList.add("blog-post");
                postElement.innerHTML = `
                    <h2>${post.title}</h2>
                    <p>${post.content}</p>
                    <small>Yayın tarihi: ${new Date(post.createdAt).toLocaleString()}</small>
                `;
                blogContainer.appendChild(postElement);
            });
        })
        .catch(error => console.error("Veri yüklenirken hata oluştu!", error));
});