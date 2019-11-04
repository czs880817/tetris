precision highp float;
varying vec2 fragCoord;
uniform float scoreTime;
// 需要消掉的行数的索引
uniform int indexArray[4];
// 20x10的每格的颜色数据
uniform int data[200];
// 是否需要做消除演示
uniform int clearMode;

#define LAND_COUNT 10.0
#define PORT_COUNT 20.0

#define PORT_SIZE 20
#define PIECE_SIZE 4

#define RED 1
#define GREEN 2
#define BLUE 3
#define YELLOW 4
#define PINK 5
#define WATER 6

void main() {
    // 首先计算当前的像素点在哪个格子上
    float fx = 1.0 / LAND_COUNT;
    float fy = 1.0 / PORT_COUNT;

    vec2 rt = vec2(0.0);
    vec2 lb = vec2(0.0);

    int landIndex = -1;
    while (rt.x <= fragCoord.x) {
        rt.x += fx;
        landIndex++;
    }
    lb.x = rt.x - fx;

    int portIndex = -1;
    while (rt.y <= fragCoord.y) {
        rt.y += fy;
        portIndex++;
    }
    lb.y = rt.y - fy;

    vec2 center = (lb + rt) / 2.0;
    int index = portIndex * int(LAND_COUNT) + landIndex;

    vec3 color = vec3(0.0);
    if (data[index] == 0) {
        // 如果当前格子没有颜色数据就涂黑
        gl_FragColor = vec4(color, 1.0);
    } else {
        // 如果当前格子有颜色数据就涂色
        if (data[index] == RED) {
            color = vec3(1.0, 0.0, 0.0);
        } else if (data[index] == GREEN) {
            color = vec3(0.0, 1.0, 0.0);
        } else if (data[index] == BLUE) {
            color = vec3(0.0, 0.0, 1.0);
        } else if (data[index] == YELLOW) {
            color = vec3(1.0, 1.0, 0.0);
        } else if (data[index] == PINK) {
            color = vec3(1.0, 0.0, 1.0);
        } else if (data[index] == WATER) {
            color = vec3(0.0, 1.0, 1.0);
        }

        float maxValue = max(abs(fragCoord.x - center.x), abs(fragCoord.y - center.y));
        float d = sqrt(maxValue * maxValue + maxValue * maxValue);
        gl_FragColor = vec4(mix(color, color * 0.8, d / distance(center, rt)), 1.0);

        // 如果有需要消除的行就闪白光
        if (clearMode == 1) {
            bool b = false;
            for (int i = 0; i != PIECE_SIZE; i++) {
                if (PORT_SIZE - indexArray[i] - 1 == portIndex) {
                    b = true;
                    break;
                }
            }

            if (b) {
                gl_FragColor += (1.0 - cos(scoreTime));
            }
        }
    }
}