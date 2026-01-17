document.addEventListener('DOMContentLoaded', function() {
    const avatarImage = document.getElementById('avatar-image');
    const input = document.getElementById('image-file');
    const userId = document.body.dataset.userid;

    input.addEventListener('change', function(event) {
        const file = event.target.files[0];

        if (!file) {
            return; // Если файл не выбран
        }

        // Проверка, что это изображение
        if (!file.type.startsWith('image/')) {
            alert('Пожалуйста, выберите файл изображения (JPEG, PNG и т.д.)');
            input.value = ''; // Сбрасываем input
            return;
        }
        // Проверка размера файла (максимум 5MB)
        if (file.size > 5 * 1024 * 1024) {
            alert('Файл слишком большой. Максимальный размер: 5MB');
            input.value = '';
            return;
        }
        const reader = new FileReader
        ();

        reader.onload = function(e) {
            avatarImage.src = e.target.result;
        };

        reader.onerror = function(error) {
            console.error('Ошибка при чтении файла:', error);
            alert('Ошибка при загрузке изображения');
            input.value = ''; // Сбрасываем input
            return;
        };


        reader.readAsDataURL(file);

        const formData = new FormData();
              formData.append("file", file);

        fetch("http://localhost:8000/settings/" + userId + "/upload", {
               method: "POST",
               body: formData,
        });
    });
});