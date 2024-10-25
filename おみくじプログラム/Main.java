//TIP コードを<b>実行</b>するには、<shortcut actionId="Run"/> を押すか
// ガターの <icon src="AllIcons.Actions.Execute"/> アイコンをクリックします。
public class Main {
    public static void main(String[] args) {
    System.out.println("あなたの運勢を占います。");
    int fortune = new java.util.Random().nextInt(5) + 1;
        switch (fortune) {
            case 1:
                System.out.println("大吉");
                break;
            case 2:
                System.out.println("中吉");
                break;
            case 3:
                System.out.println("小吉");
                break;
            case 4:
                System.out.println("凶");
                break;
            case 5:
                System.out.println("大凶");
                break;
        }

    }
}