precision mediump float;

uniform sampler2D iChannel0;

uniform float HEAT_MAX;
uniform float PointRadius;

uniform int PointCount;
uniform vec3 points[100];

uniform vec2 iResolution;


void main() {
    vec2 uv = (vec2(gl_FragCoord.x, iResolution.y-gl_FragCoord.y)) / min(iResolution.x, iResolution.y);

    float d = 0.;

    for (int i = 0; i < PointCount; i++) {
        vec3 v = points[i];
        float intensity = v.z / HEAT_MAX;
        float pd =  (1. - length(uv - v.xy) / PointRadius) * intensity;
        d += pow(max(0., pd), 2.);

    }

    d = pow(clamp(d, 0., 1.) * 3.14159 * .5, .9);
    vec3 c = vec3(sin(d), sin(d * 2.), cos(d)) * 1.1;

    //gl_FragColor = vec4(mix(texture2D(iChannel0, uv).rgb, c.rgb ,d), 0.01);
    gl_FragColor = mix(vec4(0,0,0,0),vec4(c.rgb, 1) , d);

}

vec3 gradient(float w, vec2 uv) {
    w = pow(clamp(w, 0., 1.) * 3.14159 * .5, .9);
    vec3 c = vec3(sin(w), sin(w * 2.), cos(w)) * 1.1;
    return mix(texture2D(iChannel0, uv).rgb, c.rgb, w);

}
