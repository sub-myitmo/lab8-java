package client.gui;

class AnimatedCircle {
    int x;
    double y;
    int radius;
    int colorIndex;
    boolean growing = true;

    String strStudyGroup;

    public AnimatedCircle(int x, double y, int radius, int colorIndex, String strStudyGroup) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.colorIndex = colorIndex;
        this.strStudyGroup = strStudyGroup;
    }
}
