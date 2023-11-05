import java.io.*;
import java.util.Scanner;
import java.util.Formatter;

public class Main {
    static int countCoef;
    static int countLimit;
    static boolean desire;
    static double[][] matrix;
    static int countRow;
    static int countCol;
    static int[] mins;

    public static void main(String[] args) {
        inpMatrixManual();
        int i = 1;
        while (!indexStrCalc()) {
            System.out.println("\nИтерация " + i++);
            if (!calcCheckMinIndxElCol()) {
                System.out.println("Нет решения");
                return;
            }
            calcAttitMin();
            printMatrix(true);
            calcNewCoef();
        }
        System.out.println("\nИтерация " + i);
        printMatrix(false);
        double answer = 0;
        for (int j = 1; j < countLimit + 1; ++j) {
            if (matrix[j][1] <= countCoef) {
                if (!desire) {
                    answer += matrix[j][2] * -matrix[0][(int) matrix[j][1] + 2];
                }
                else {
                    answer += matrix[j][2] * matrix[0][(int) matrix[j][1] + 2];
                }
            }
        }
        System.out.println("\nОтвет: " + answer);
    }
    private static void inpMatrixManual() {
        inpData();
        Scanner scanner = new Scanner(System.in);
        if (desire) {
            countRow = countLimit + 2;
            countCol = countCoef + countLimit + 4;
            matrix = new double[countRow][countCol];
        }
        else {
            countRow = countLimit + 2;
            countCol = countCoef + 2 * countLimit + 4;
            matrix = new double[countRow][countCol];
        }
        System.out.println("Введите коэффициенты целевой функции");
        matrix[0][2] = 0;
        for (int i = 3; i < countCoef + 3; ++i) {
            System.out.print("Введите коэффициент целевой функции " + (i - 2) + ": ");
            matrix[0][i] = scanner.nextDouble();
            if (!desire) {
                matrix[0][i] = -matrix[0][i];
            }
        }
        for (int i = countCoef + 3; i < countLimit + countCoef + 3; ++i) {
            matrix[0][i] = 0;
        }
        if (!desire) {
            for (int i = countCoef + 3 + countLimit; i < 2 * countLimit + countCoef + 3; ++i) {
                matrix[0][i] = -100000;
            }
        }
        for (int i = 1; i < countLimit + 1; ++i) {
            System.out.println("Ограничение " + i);
            for (int j = 3; j < countCoef + 3; ++j) {
                System.out.print("Введите коэффициент " + (j-2) + ": ");
                matrix[i][j] = scanner.nextDouble();
            }
            System.out.print("Правая часть ограничения " + i + ": ");
            matrix[i][2] = scanner.nextDouble();
            if (!desire) {
                matrix[i][countCoef + 2 + i] = -1;
                matrix[i][countCoef + 2 + i + countLimit] = 1;
            }
            else {
                matrix[i][countCoef + 2 + i] = 1;
            }
        }
        int coef = countLimit + 1;
        for (int i = 1; i < countLimit + 1; ++i) {
            matrix[i][0] = matrix[0][countCol - coef];
            matrix[i][1] = countCol - coef-- - 2;
        }
    }
    private static boolean indexStrCalc() {
        for (int i = 2; i < countCol - 1; ++i) {
            double sum = 0;
            for (int j = 1; j < countLimit + 1; ++j) {
                sum += matrix[j][0] * matrix[j][i];
            }
            matrix[countLimit + 1][i] = sum - matrix[0][i];
        }
        boolean check = true;
        for (int i = 3; i < countCol - 1; ++i) {
            if (matrix[countLimit + 1][i] < 0) {
                check = false;
                break;
            }
        }
        return check;
    }
    private static boolean calcCheckMinIndxElCol() {
        mins = new int[2];
        mins[0] = 3;
        for (int i = 4; i < countCol - 1; ++i) {
            if (matrix[countLimit + 1][i] < matrix[countLimit + 1][mins[0]]) {
                mins[0] = i;
            }
        }
        boolean check = false;
        for (int i = 1; i < countLimit + 1; ++i) {
            if (matrix[i][mins[0]] >= 0) {
                check = true;
                break;
            }
        }
        return check;
    }
    private static void calcAttitMin() {
        mins[1] = 1;
        for (int i = 1; i < countLimit + 1; ++i) {
            matrix[i][countCol - 1] = matrix[i][mins[0]] >=0 ? matrix[i][2] / matrix[i][mins[0]]: 100000;
            if (matrix[i][countCol - 1] < matrix[mins[1]][countCol - 1]) {
                mins[1] = i;
            }
        }
    }
    private static void calcNewCoef() {
        matrix[mins[1]][0] = matrix[0][mins[0]];
        matrix[mins[1]][1] = mins[0] - 2;
        for (int i = 1; i < countLimit + 1; ++i) {
            if (i != mins[1]) {
                double elem = matrix[i][mins[0]];
                for (int j = 2; j < countCol - 1; ++j) {
                    matrix[i][j] = (matrix[i][j] * matrix[mins[1]][mins[0]] - matrix[mins[1]][j] * elem) / matrix[mins[1]][mins[0]];
                }
            }
        }
        double elem = matrix[mins[1]][mins[0]];
        for (int i = 2; i < countCol - 1; ++i) {
            matrix[mins[1]][i] = matrix[mins[1]][i] / elem;
        }
    }
    private static void printMatrix(boolean flag) {
        System.out.printf("%" + 13 + "s", "Cij");
        System.out.print("             ");
        for (int i = 2; i < countCol - 1; ++i) {
            System.out.printf("%" + 13 + "." + 3 + "f", matrix[0][i]);
        }
        System.out.printf("%" + 13 + "s", "Тета\n");
        System.out.printf("%" + 26 + "s", "By");
        for (int i = 0; i < countCol - 3; ++i) {
            System.out.printf("%" + 12 + "s", "A");
            System.out.print(i);
        }
        System.out.println();
        int col = flag ? countCol : countCol - 1;
        for (int i = 1; i < countLimit + 1; ++i) {
            for (int j = 0; j < col; ++j) {
                System.out.printf("%" + 13 + "." + 3 + "f", matrix[i][j]);
            }
            System.out.println();
        }
        System.out.printf("%" + 26 + "s", "Дельта j");
        for (int i = 2; i < countCol - 1; ++i) {
            System.out.printf("%" + 13 + "." + 3 + "f", matrix[countLimit + 1][i]);
        }
        System.out.println("\nДопустимое базисное решение:");
        System.out.print("(");
        for (int i = 0; i < countCol - 4; ++i) {
            boolean check = false;
            for (int j = 1; j < countLimit + 1; ++j) {
                if (i + 1 == matrix[j][1]) {
                    if (i != countCol - 5) {
                        System.out.print(matrix[j][2] + ", ");
                    }
                    else {
                        System.out.print(matrix[j][2] + ")");
                    }
                    check = true;
                    break;
                }
            }
            if (!check) {
                if (i != countCol - 5) {
                    System.out.print(0.0 + ", ");
                }
                else {
                    System.out.print(0.0 + ")");
                }
            }
        }
        System.out.println();
    }
    private static void inpData() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите количество коэффициентов целевой функции: ");
        countCoef = scanner.nextInt();
        System.out.print("Введите количество ограничений: ");
        countLimit = scanner.nextInt();
        System.out.print("Функция стремится к min(false) или к max(true)?: ");
        desire = scanner.nextBoolean();
    }
}