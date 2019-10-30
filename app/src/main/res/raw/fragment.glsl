precision highp float;
varying vec2 fragCoord;
varying vec4 screen;
uniform float iTime;

#define LAND_COUNT 10.0
#define PORT_COUNT 20.0

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

    vec3 color = vec3(0.0, 0.0, mix(1.0, 0.5, distance(center, fragCoord) / distance(center, rt)));

    gl_FragColor = vec4(color, 1.0);
}