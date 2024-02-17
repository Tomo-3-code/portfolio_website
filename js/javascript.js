

/*▼ごあいさつ（自動スライド処理）▼*/ 

const slide = document.getElementById('slide');
const prev = document.getElementById('prev');
const next = document.getElementById('next');
const indicator = document.getElementById('indicator');
const lists = document.querySelectorAll('.list');
const totalSlides = lists.length;
let count = 0;
let autoPlayInterval;
function updateListBackground() {
for (let i = 0; i < lists.length; i++) {
    lists[i].style.backgroundColor = i === count % totalSlides ? '#000' : '#fff';
}
}
function nextClick() {
slide.classList.remove(`slide${count % totalSlides + 1}`);
count++;
slide.classList.add(`slide${count % totalSlides + 1}`);
updateListBackground();
}
function prevClick() {
slide.classList.remove(`slide${count % totalSlides + 1}`);
count--;
if (count < 0) count = totalSlides - 1;
slide.classList.add(`slide${count % totalSlides + 1}`);
updateListBackground();
}
function startAutoPlay() {
autoPlayInterval = setInterval(nextClick, 3000);
}
function resetAutoPlayInterval() {
clearInterval(autoPlayInterval);
startAutoPlay();
}
next.addEventListener('click', () => {
nextClick();
resetAutoPlayInterval();
});
prev.addEventListener('click', () => {
prevClick();
resetAutoPlayInterval();
});
indicator.addEventListener('click', (event) => {
if (event.target.classList.contains('list')) {
    const index = Array.from(lists).indexOf(event.target);
    slide.classList.remove(`slide${count % totalSlides + 1}`);
    count = index;
    slide.classList.add(`slide${count % totalSlides + 1}`);
    updateListBackground();
    resetAutoPlayInterval();
}
});
startAutoPlay();

/*▲ごあいさつ（自動スライド処理）▲*/ 

// ▼マウスポインター(クリック時水滴表示処理)▼ //

//click イベントで発火
document.body.addEventListener("click", drop, false);

function drop(e) {

    //座標の取得
    var x = e.pageX;
    var y = e.pageY;

    //しずくになるdivの生成、座標の設定
    var sizuku = document.createElement("div");
    sizuku.style.top = y + "px";
    sizuku.style.left = x + "px";
    document.body.appendChild(sizuku);

    //アニメーションをする className を付ける
    sizuku.className = "sizuku";

    //アニメーションが終わった事を感知してしずくを remove する
    sizuku.addEventListener("animationend", function() {
        this.parentNode.removeChild(this);
    }, false);
}
// ▼マウスポインター(クリック時水滴表示処理)▼ //