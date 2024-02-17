

// ▼ホームアイコン表示▼　//

$(window).on("load", function () {
        $(".js-fade").fadeIn(2000);
    });

// ▲ホームアイコン表示▲ //

// ▼コンテンツメニューピンポイントターゲット（アンカーリンク処理）▼ //

$(function(){
    $("a[href^='#']").click(function(){
        const target = $(this.hash);
        const position = $(target).offset().top;
    $('html,body').animate({scrollTop: position}, 400);
        return false;
    });
});

// ▲コンテンツメニューピンポイントターゲット（アンカーリンク処理）▲ //


// ▼コンテンツスクロール（自動表示処理）▼ //

$(function(){
        $(window).scroll(function (){
            $('.flex0').each(function(){
                var pos = $(this).offset().top;
                var scroll = $(window).scrollTop();
                var windowHeight = $(window).height();
                if (scroll > pos - windowHeight + 100){
                $(this).addClass('scroll');
                }
            });
        });
    });

    // ▲コンテンツスクロール（自動表示処理▲　//




    // ▼ページトップホバー (ページトップ移動処理)▼ //

    $(function(){
    var pagetop = $('#page-top');
        pagetop.hide();
    $(window).scroll(function () {
    if ($(this).scrollTop() > 100) {
        pagetop.fadeIn();
    } else {
        pagetop.fadeOut();
        }
    });
    pagetop.click(function () {
$('body, html').animate({ scrollTop: 0 }, 500);
        return false;
        });
    });

    // ▲ページトップホバー (ページトップ移動処理)▲ //



