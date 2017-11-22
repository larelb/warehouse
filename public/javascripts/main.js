$(function() {
    $("body").on("click", "#delete-link", function(e) {
        e.preventDefault();
        var URL = $(this).attr("href");
        var csrfToken = $("[name='csrfToken']").val();
        console.log(csrfToken);
        $.ajax({
            url: URL,
            headers: {
               "Csrf-Token": csrfToken
            },
            type: 'DELETE',
            success: function(results) {
              // Refresh the page
              window.location.href = "/products/";
            }
        });
        return false;
    });
});