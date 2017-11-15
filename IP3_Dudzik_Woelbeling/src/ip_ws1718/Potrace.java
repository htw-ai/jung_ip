package ip_ws1718;

import java.util.ArrayList;

class Potrace {

    public static ArrayList<ArrayList<PPDirection>> fillRegions(int[] argb, int height, int width) {
        ArrayList<ArrayList<PPDirection>> objects = new ArrayList<ArrayList<PPDirection>>();
        // traverse image and look for foreground pixels
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                int pixel = argb[getIndex(i, j, width)];
                if (isForegroundColor(pixel)) {
                    ArrayList<PPDirection> list = new ArrayList<PPDirection>();

                    // Top right to top left because we didn't encounter another forground pixel
                    PPosition stubPosition     = new PPosition(i, j).top().right();
                    PPosition startingPosition = new PPosition(i, j).top().left();

                    PPDirection startDirection = new PPDirection(stubPosition, startingPosition).next(argb, width);

                    list.add(startDirection);

                    // make sure that we go at least one time around
                    while (list.size() < 4 || list.get(0).from.equals(list.get(list.size()-1).from)) {
                        PPDirection last = list.get(list.size()-1);
                        PPDirection next = last.next(argb, width);

                        list.add(next);

                    }

                    list.remove(list.size()-1);

                    list.forEach((next) -> {
                        if (next.isYMovement()) {
                            if (next.from.left) {
                                invertRow(argb, width, next.from.j, next.from.i);
                            } else {
                                invertRow(argb, width, next.from.j, next.from.i + 1);
                            }
                        }
                    });

                    objects.add(list);
                }
            }
        }
        return objects;
    }

    public static int getIndex(int x, int y, int width) {
        return y * width + x;
    }

    static class PPosition {
        public boolean left;
        public boolean right;
        public boolean top;
        public boolean bottom;
        public int i;
        public int j;


        PPosition(int i, int j) {
            this.i = i;
            this.j = j;
            left = false;
            right = false;
            bottom = false;
            top = false;
        }

        PPosition(PPosition p) {
            i = p.i;
            j = p.j;
            left = p.left;
            right = p.right;
            bottom = p.bottom;
            top = p.top;
        }

        public PPosition top() {
            PPosition p = new PPosition(this);
            p.top = true;
            p.bottom = false;
            return p;
        }


        public PPosition bottom() {
            PPosition p = new PPosition(this);
            p.top = false;
            p.bottom = true;
            return p;
        }


        public PPosition left() {
            PPosition p = new PPosition(this);
            p.left = true;
            p.right = false;
            return p;
        }


        public PPosition right() {
            PPosition p = new PPosition(this);
            p.right = true;
            p.left = false;
            return p;
        }

        public boolean equals(PPosition position) {
            return right == position.right && left == position.left && top == position.top && bottom == position.bottom
                    && i == position.i && j == position.j;
        }

        public int getColor(int[] argb, int width) {
            int index = getIndex(i, j, width);
            // if index not present it returns 0xFFFFFFFF
            return argb[index];
        }
    }

    static class PPField {
        public int topLeftColor;
        public int topRightColor;
        public int bottomLeftColor;
        public int bottomRightColor;

        PPField(int topLeft, int topRight, int bottomLeft, int bottomRight) {
            topLeftColor     = topLeft;
            topRightColor    = topRight;
            bottomLeftColor  = bottomLeft;
            bottomRightColor = bottomRight;
        }

        public boolean oneWhite () {
            return isForegroundColor(topLeftColor) && isForegroundColor(topRightColor) &&
                   isForegroundColor(bottomLeftColor) && !isForegroundColor(bottomRightColor);
        }
        public boolean twoWhite () {
            return isForegroundColor(topLeftColor) && !isForegroundColor(topRightColor) &&
                    isForegroundColor(bottomLeftColor) && !isForegroundColor(bottomRightColor);
        }
        public boolean threeWhite () {
            return !isForegroundColor(topLeftColor) && !isForegroundColor(topRightColor) &&
                    isForegroundColor(bottomLeftColor) && !isForegroundColor(bottomRightColor);
        }
        public boolean undeciesiveWhite () {
            return isForegroundColor(topLeftColor) && !isForegroundColor(topRightColor) &&
                    !isForegroundColor(bottomLeftColor) && isForegroundColor(bottomRightColor);
        }

        public String direction() {
            if (oneWhite()) {
                return "right";
            } else if (twoWhite()) {
                return "straight";
            } else if (threeWhite()) {
                return "left";
            } else {
                if (!undeciesiveWhite()) {
                    System.out.println("No Such Direction");
                    System.out.println(topLeftColor);
                    System.out.println(topRightColor);
                    System.out.println(bottomLeftColor);
                    System.out.println(bottomRightColor);
                }
                return "left";
            }
        }
    }

    static class PPDirection {
        public PPosition from;
        public PPosition to;

        PPDirection(PPosition from, PPosition to) {
            this.from = from;
            this.to = to;
        }

        public boolean isYMovement() {
            return isHeadedBottom() || isHeadedTop();
        }

        public boolean isHeadedTop() {
            return from.bottom && to.top;
        }

        public boolean isHeadedBottom() {
            return to.bottom && from.top;
        }

        public boolean isHeadedRight() {
            return from.left && to.right;
        }

        public boolean isHeadedLeft() {
            return from.right && to.left;
        }

        public PPDirection next(int[] argb, int width) {
            // where i column j row
            int i = from.i;
            int j = from.j;
            if (isHeadedTop()) {
                if (from.left) {
                    PPosition topLeft     = new PPosition(i - 1, j - 1);
                    PPosition bottomLeft  = new PPosition(i - 1, j);
                    PPosition topRight    = new PPosition(i, j - 1);
                    PPosition bottomRight = new PPosition(i, j);

                    int topLeftColor = topLeft.getColor(argb, width);
                    int topRightColor = topRight.getColor(argb, width);
                    int bottomLeftColor = bottomLeft.getColor(argb, width);
                    int bottomRightColor = bottomRight.getColor(argb, width);
                    String turn = new PPField(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor).direction();

                    if (turn == "right") {
                        PPosition startPosition = bottomRight.top().left();
                        PPosition endPosition = bottomRight.top().right();
                        return new PPDirection(startPosition, endPosition);
                    } else if (turn == "left") {
                        PPosition startPosition = bottomLeft.top().right();
                        PPosition endPosition = bottomLeft.top().left();
                        return new PPDirection(startPosition, endPosition);
                    } else {// straight
                        PPosition startPosition = topRight.bottom().left();
                        PPosition endPosition = topRight.top().left();
                        return new PPDirection(startPosition, endPosition);
                    }
                } else {
                    PPosition topLeft = new PPosition(i, j - 1);
                    PPosition bottomLeft = new PPosition(i, j);
                    PPosition topRight = new PPosition(i + 1, j - 1);
                    PPosition bottomRight = new PPosition(i + 1, j);

                    int topLeftColor = topLeft.getColor(argb, width);
                    int topRightColor = topRight.getColor(argb, width);
                    int bottomLeftColor = bottomLeft.getColor(argb, width);
                    int bottomRightColor = bottomRight.getColor(argb, width);
                    String turn = new PPField(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor).direction();

                    if (turn == "right") {
                        PPosition startPosition = bottomRight.top().left();
                        PPosition endPosition = bottomRight.top().right();
                        return new PPDirection(startPosition, endPosition);
                    } else if (turn == "left") {
                        PPosition startPosition = bottomLeft.top().right();
                        PPosition endPosition = bottomLeft.top().left();
                        return new PPDirection(startPosition, endPosition);
                    } else {// straight
                        PPosition startPosition = topLeft.bottom().right();
                        PPosition endPosition = topLeft.top().right();
                        return new PPDirection(startPosition, endPosition);
                    }
                }
            } else if (isHeadedLeft()) {
                if (from.top) {
                    PPosition topLeft = new PPosition(i - 1, j);
                    PPosition bottomLeft = new PPosition(i, j);
                    PPosition topRight = new PPosition(i - 1, j - 1);
                    PPosition bottomRight = new PPosition(i, j - 1);

                    int topLeftColor = topLeft.getColor(argb, width);
                    int topRightColor = topRight.getColor(argb, width);
                    int bottomLeftColor = bottomLeft.getColor(argb, width);
                    int bottomRightColor = bottomRight.getColor(argb, width);
                    String turn = new PPField(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor).direction();

                    if (turn == "right") {
                        PPosition startPosition = bottomRight.bottom().left();
                        PPosition endPosition = bottomRight.top().left();
                        return new PPDirection(startPosition, endPosition);
                    } else if (turn == "left") {
                        PPosition startPosition = bottomLeft.top().left();
                        PPosition endPosition = bottomLeft.bottom().left();
                        return new PPDirection(startPosition, endPosition);
                    } else {// straight
                        PPosition startPosition = topLeft.top().right();
                        PPosition endPosition = topLeft.top().left();
                        return new PPDirection(startPosition, endPosition);
                    }
                } else {
                    PPosition topLeft = new PPosition(i - 1, j + 1);
                    PPosition bottomLeft = new PPosition(i, j + 1);
                    PPosition topRight = new PPosition(i - 1, j);
                    PPosition bottomRight = new PPosition(i, j);

                    int topLeftColor = topLeft.getColor(argb, width);
                    int topRightColor = topRight.getColor(argb, width);
                    int bottomLeftColor = bottomLeft.getColor(argb, width);
                    int bottomRightColor = bottomRight.getColor(argb, width);
                    String turn = new PPField(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor).direction();

                    if (turn == "right") {
                        PPosition startPosition = bottomRight.bottom().left();
                        PPosition endPosition = bottomRight.top().left();
                        return new PPDirection(startPosition, endPosition);
                    } else if (turn == "left") {
                        PPosition startPosition = bottomLeft.top().left();
                        PPosition endPosition = bottomLeft.bottom().left();
                        return new PPDirection(startPosition, endPosition);
                    } else {// straight
                        PPosition startPosition = topLeft.top().right();
                        PPosition endPosition = topLeft.top().left();
                        return new PPDirection(startPosition, endPosition);
                    }
                }
            } else if (isHeadedRight()) {
                if (from.top) {
                    PPosition topLeft = new PPosition(i + 1, j - 1);
                    PPosition bottomLeft = new PPosition(i, j - 1);
                    PPosition topRight = new PPosition(i + 1, j);
                    PPosition bottomRight = new PPosition(i, j);

                    int topLeftColor = topLeft.getColor(argb, width);
                    int topRightColor = topRight.getColor(argb, width);
                    int bottomLeftColor = bottomLeft.getColor(argb, width);
                    int bottomRightColor = bottomRight.getColor(argb, width);
                    String turn = new PPField(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor).direction();

                    if (turn == "right") {
                        PPosition startPosition = bottomRight.top().right();
                        PPosition endPosition = bottomRight.bottom().right();
                        return new PPDirection(startPosition, endPosition);
                    } else if (turn == "left") {
                        PPosition startPosition = bottomLeft.bottom().right();
                        PPosition endPosition = bottomLeft.bottom().left();
                        return new PPDirection(startPosition, endPosition);
                    } else {// straight
                        PPosition startPosition = topRight.top().left();
                        PPosition endPosition = topRight.top().right();
                        return new PPDirection(startPosition, endPosition);
                    }
                } else {
                    PPosition topLeft = new PPosition(i + 1, j);
                    PPosition bottomLeft = new PPosition(i, j);
                    PPosition topRight = new PPosition(i + 1, j + 1);
                    PPosition bottomRight = new PPosition(i, j + 1);

                    int topLeftColor = topLeft.getColor(argb, width);
                    int topRightColor = topRight.getColor(argb, width);
                    int bottomLeftColor = bottomLeft.getColor(argb, width);
                    int bottomRightColor = bottomRight.getColor(argb, width);
                    String turn = new PPField(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor).direction();

                    if (turn == "right") {
                        PPosition startPosition = bottomRight.top().right();
                        PPosition endPosition = bottomRight.bottom().right();
                        return new PPDirection(startPosition, endPosition);
                    } else if (turn == "left") {
                        PPosition startPosition = bottomLeft.bottom().right();
                        PPosition endPosition = bottomLeft.top().right();
                        return new PPDirection(startPosition, endPosition);
                    } else {// straight
                        PPosition startPosition = topLeft.bottom().left();
                        PPosition endPosition = topLeft.bottom().right();
                        return new PPDirection(startPosition, endPosition);
                    }
                }
            } else { // isHeadedBottom
                if (from.left) {
                    PPosition topLeft     = new PPosition(i, j + 1);
                    PPosition bottomLeft  = new PPosition(i, j);
                    PPosition topRight    = new PPosition(i - 1, j + 1);
                    PPosition bottomRight = new PPosition(i - 1, j);

                    int topLeftColor = topLeft.getColor(argb, width);
                    int topRightColor = topRight.getColor(argb, width);
                    int bottomLeftColor = bottomLeft.getColor(argb, width);
                    int bottomRightColor = bottomRight.getColor(argb, width);
                    String turn = new PPField(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor).direction();

                    if (turn == "right") {
                        PPosition startPosition = bottomRight.bottom().right();
                        PPosition endPosition = bottomRight.bottom().left();
                        return new PPDirection(startPosition, endPosition);
                    } else if (turn == "left") {
                        PPosition startPosition = bottomLeft.bottom().left();
                        PPosition endPosition = bottomLeft.bottom().right();
                        return new PPDirection(startPosition, endPosition);
                    } else {// straight
                        PPosition startPosition = topLeft.top().left();
                        PPosition endPosition = topLeft.bottom().left();
                        return new PPDirection(startPosition, endPosition);
                    }
                } else {
                    PPosition topLeft = new PPosition(i + 1, j + 1);
                    PPosition bottomLeft = new PPosition(i + 1, j);
                    PPosition topRight = new PPosition(i, j + 1);
                    PPosition bottomRight = new PPosition(i, j);

                    int topLeftColor = topLeft.getColor(argb, width);
                    int topRightColor = topRight.getColor(argb, width);
                    int bottomLeftColor = bottomLeft.getColor(argb, width);
                    int bottomRightColor = bottomRight.getColor(argb, width);
                    String turn = new PPField(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor).direction();

                    if (turn == "right") {
                        PPosition startPosition = bottomRight.bottom().right();
                        PPosition endPosition = bottomRight.bottom().left();
                        return new PPDirection(startPosition, endPosition);
                    } else if (turn == "left") {
                        PPosition startPosition = bottomLeft.bottom().left();
                        PPosition endPosition = bottomLeft.bottom().right();
                        return new PPDirection(startPosition, endPosition);
                    } else {// straight
                        PPosition startPosition = topRight.top().right();
                        PPosition endPosition = topRight.bottom().right();
                        return new PPDirection(startPosition, endPosition);
                    }
                }
            }
        }
    }

    public static boolean isForegroundColor (int col) {
        // black
        return col == 0xff000000;
    }

    public static void invertRow(int[] argb, int width, int row, int column) {
        int mask = 0xff;

        for (int i = column; i < width; i++) {
                int ind = getIndex(i, row, width);

                int color = argb[ind];
                int red = (color >> 16) & mask;
                int green = (color >> 8) & mask;
                int blue = color & mask;
                // invert each channel
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;
                //write
                argb[ind] = (mask << 24) | (red << 16) | (green << 8) | blue;
        }
    }

}
