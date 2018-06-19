package com.jornco.controller.code;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kkopite on 2017/10/25.
 */

public class IronbotCode {

    private List<byte[]> codes = new ArrayList<>();
    private String data = "";

    public String getData() {
        return data;
    }

    public List<byte[]> getCodes() {
        return codes;
    }

    public static IronbotCode create(String code) {
        IronbotCode ironbotCode = new IronbotCode();
        ironbotCode.data = code;
        ironbotCode.codes = split(code);
        return ironbotCode;
    }

    public static IronbotCode create(byte[] code) {
        IronbotCode ironbotCode = new IronbotCode();
        ironbotCode.data = new String(code);
        ironbotCode.codes = split(code);
        return ironbotCode;
    }

    public static class Builder {

        private SparseBooleanArray hasInitDHT11 = new SparseBooleanArray();

        private StringBuilder sb = new StringBuilder();

        public Builder addColor(int r, int g, int b) {
            sb.append("#B").append(r).append(",").append(g).append(",").append(b).append(",*");
            return this;
        }

        public Builder addServoStart() {
            sb.append("#A");
            return this;
        }

        public Builder addServo(int num, int angle, int time) {
            sb.append(num).append(",").append(angle).append(",").append(time).append(",");
            return this;
        }

        public Builder addServoEnd() {
            sb.append("*");
            return this;
        }

        public Builder delay(int time) {
            sb.append("Delayms(").append(time).append(")\n");
            return this;
        }

        public Builder ultra(int a) {
            sb.append("readUltra(").append(a).append(")\n");
            return this;
        }

        public Builder readTemp(int a) {
            checkInit(a);
            sb.append("readTemp(").append(a).append(")\n");
            return this;
        }

        public Builder readHumi(int a) {
            checkInit(a);
            sb.append("readHumi(").append(a).append(")\n");
            return this;
        }

        private void checkInit(int a) {
            boolean b = hasInitDHT11.get(a);
            if (b) {
                return;
            }
            hasInitDHT11.put(a, true);
            sb.append("initdht11(").append(a).append(")\n");
        }

        public Builder tubeAll(int a, int b) {
            sb.append("tube_all(").append(a).append(",").append(b).append(")\n");
            return this;
        }

        // 具体传的是啥? 0xFFAAFF 这种
        public Builder Led(int a, int r, int g, int b) {
            String r1 = Integer.toHexString(r);
            String g1 = Integer.toHexString(g);
            String b1 = Integer.toHexString(b);
            r1 = r1.length() == 1 ? "0" + r1 : r1;
            g1 = g1.length() == 1 ? "0" + g1 : g1;
            b1 = b1.length() == 1 ? "0" + b1 : b1;
            sb.append("Led(").append(a)
                    .append(",0x")
                    .append(r1)
                    .append(g1)
                    .append(b1)
                    .append(")\n");
            return this;
        }

        public Builder Key(int a, int b) {
            if (b != 0 && b != 1) {
                throw new IllegalArgumentException("b 值只能为 1 或 0 ");
            }
            sb.append("Key(").append(a).append(",").append(b).append(")\n");
            return this;
        }

        public Builder UartSend(char a) {
            sb.append("UartSend(").append(a).append(")\n");
            return this;
        }

        public Builder UartRec(char a) {
            sb.append("UartRec(").append(a).append(")\n");
            return this;
        }

        public Builder PWM(int num, int time) {
            sb.append("PWM").append(num).append("(").append(time).append(")\n");
            return this;
        }

        public IronbotCode create() {
            sb.insert(0, "--\n\n").append("\n--");
            String msg = sb.toString();
            IronbotCode code = new IronbotCode();
            code.codes = split(msg);
            code.data = msg;
            return code;
        }

        public IronbotCode build() {
            String msg = sb.toString();
            IronbotCode code = new IronbotCode();
            code.codes = split(msg);
            code.data = msg;
            return code;
        }

        public String getMsg(){
            return sb.toString();
        }
    }

    private static List<byte[]> split(byte[] msg) {
        List<byte[]> codes = new ArrayList<>();
        int length = msg.length;
        if (length <= 20) {
            codes.add(msg);
        } else {
            int index = 0;
            while (index < length) {
                int end = index + 20;
                if (index + 20 > length) {
                    end = length;
                }
                codes.add(Arrays.copyOfRange(msg, index, end));
                index += 20;
            }
        }
        return codes;
    }

    private static List<byte[]> split(String msg) {
        List<byte[]> codes = new ArrayList<>();
        int length = msg.length();
        if (length <= 20) {
            codes.add(msg.getBytes());
        } else {
            int index = 0;
            while (index < length) {
                if (index + 20 > length) {
                    codes.add(msg.substring(index, length).getBytes());
                } else {
                    codes.add(msg.substring(index, index + 20).getBytes());
                }
                index += 20;
            }
        }
        return codes;
    }
}
