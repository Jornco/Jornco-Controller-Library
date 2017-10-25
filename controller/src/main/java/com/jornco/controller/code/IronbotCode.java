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


        public IronbotCode build() {
            String msg = sb.toString();
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
            IronbotCode code = new IronbotCode();
            code.codes = codes;
            return code;
        }
    }
}
