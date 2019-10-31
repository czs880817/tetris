precision highp float;
varying vec2 fragCoord;
uniform float screen;
uniform int data[200];

#define LAND_COUNT 10.0
#define PORT_COUNT 20.0
#define COLOR_BASE 256.0

vec3 getColor(int iColor) {
    float fColor = float(iColor);
    vec3 res = vec3(0.0);

    float temp = mod(fColor, COLOR_BASE);
    res.b = temp / COLOR_BASE;
    fColor = (fColor - temp) / COLOR_BASE;

    temp = mod(fColor, COLOR_BASE);
    res.g = temp / COLOR_BASE;
    fColor = (fColor - temp) / COLOR_BASE;

    res.r = fColor / COLOR_BASE;
    return res;
}

void main() {
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

    if (data[index] == 0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        vec3 color = getColor(data[index]);
        gl_FragColor = vec4(mix(color + 0.5, color, distance(center, fragCoord) / distance(center, rt)), 1.0);
    }
}