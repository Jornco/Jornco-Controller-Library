package com.jornco.controller.code;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkopite on 2017/10/25.
 */

public class IronbotCode {

    private List<String> codes = new ArrayList<>();

    public List<String> getCodes() {
        return codes;
    }

    public static class Builder {

        private StringBuilder sb = new StringBuilder();

        private boolean hasInitDHT11 = false;

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
            sb.append("readTemp(").append(a).append(")\n");
            return this;
        }

        public Builder readHumi(int a) {
            sb.append("readHumi(").append(a).append(")\n");
            return this;
        }

        public Builder tubeAll(int a, int b) {
            sb.append("tube_all(").append(a).append(",").append(b).append(")\n");
            return this;
        }

        // 具体传的是啥? 0xFFAAFF 这种
        public Builder Led(int a, int r, int g, int b) {
            sb.append("Led(").append(a).append(")\n");
            return this;
        }

        public Builder Key(int a, int b) {
            if (b != 0 && b!= 1) {
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
            sb.insert(0, "--\n").append("--");
            String msg = sb.toString();
            IronbotCode code = new IronbotCode();
            code.codes = sqlit(msg);
            return code;
        }

        public IronbotCode build() {
            String msg = sb.toString();
            IronbotCode code = new IronbotCode();
            code.codes = sqlit(msg);
            return code;
        }
    }

    private static List<String> sqlit(String msg) {
        List<String> codes = new ArrayList<>();
        int length = msg.length();
        if (length <= 20) {
            codes.add(msg);
        } else {
            int index = 0;
            while (index < length) {
                if (index + 20 > length) {
                    codes.add(msg.substring(index, length));
                } else {
                    codes.add(msg.substring(index, index + 20));
                }
                index += 20;
            }
        }
        return codes;
    }
}
