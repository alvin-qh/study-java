;
(function () {
    function getCookie(name) {
        const reg = new RegExp(`(^| )${name}=([^;]+)`);
        if (value = reg.exec(document.cookie)) {
            return value[2];
        }
        return "";
    }

    const select = document.querySelector(".lang>select");
    select.addEventListener("change", e => {
        location.href = `?lang=${e.target.value}`;
    }, false);

    lang = getCookie("__language__");
    if (lang) {
        select.value = lang;
    }
})();
