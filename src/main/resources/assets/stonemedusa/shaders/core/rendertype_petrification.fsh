#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;
uniform float CrackOpacity;

uniform vec4 ColorModulator;
uniform float Progress;
uniform float MaxRadius;
uniform vec3 EpicenterLocal;

uniform float DepetriProgress;
uniform float DepetriMaxRadius;
uniform vec3 DepetriEpicenterLocal;

uniform float EntityYaw;

in vec2 texCoord0;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec3 localPos;
in vec3 vertexNormal;

out vec4 fragColor;

vec2 rotate2D(vec2 v, float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return vec2(v.x * c - v.y * s, v.x * s + v.y * c);
}

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0);
    if (baseColor.a < 0.1) discard;

    vec4 tintedColor = baseColor * vertexColor * ColorModulator;

    tintedColor.rgb = mix(overlayColor.rgb, tintedColor.rgb, overlayColor.a);

    vec3 unrotatedPos = localPos;
    unrotatedPos.xz = rotate2D(unrotatedPos.xz, -EntityYaw);

    vec3 snappedPos = floor(unrotatedPos     * 16.0) / 16.0;

    float currentPetriRadius = (Progress / 100.0) * MaxRadius;
    float currentDepetriRadius = (DepetriProgress / 100.0) * DepetriMaxRadius;

    float distPetri = distance(snappedPos, EpicenterLocal);
    float distDepetri = distance(snappedPos, DepetriEpicenterLocal);

    bool inPetri = (distPetri <= currentPetriRadius && Progress > 0.0);
    bool inDepetri = (distDepetri <= currentDepetriRadius && DepetriProgress > 0.0);

    vec4 finalColor;

    if (inPetri || inDepetri) {
        float edgePetri = currentPetriRadius - distPetri;
        float edgeDepetri = currentDepetriRadius - distDepetri;

        bool isPetriEdge = inPetri && (edgePetri <= (1.0 / 16.0)) && Progress < 100.0;

        bool isDepetriEdge = inDepetri && (edgeDepetri <= (2.0 / 16.0)) && DepetriProgress < 100.0;

        if (isDepetriEdge && inPetri) {
            finalColor = vec4(1.0, 1.0, 0.2, tintedColor.a);
        } else if (isPetriEdge && !inDepetri) {
            finalColor = vec4(0.2, 1.0, 0.2, tintedColor.a);
        } else if (inPetri && !inDepetri) {
            float luminance = dot(tintedColor.rgb, vec3(0.299, 0.587, 0.114));
            vec3 stoneTint = vec3(luminance) * vec3(0.65, 0.65, 0.65);

            if (CrackOpacity > 0.0) {
                vec2 texSize = vec2(textureSize(Sampler0, 0));

                vec2 pixelUV = texCoord0 * texSize;

                float crackTileSize = 16.0;

                vec2 tiledUV = fract(pixelUV / crackTileSize);
                vec4 crackTex = texture(Sampler3, tiledUV);

                vec3 softenedCracks = min(crackTex.rgb + 0.35, vec3(1.0));

                stoneTint = mix(stoneTint, stoneTint * softenedCracks, crackTex.a * CrackOpacity);
            }
            // ------------------------------

            finalColor = vec4(stoneTint * lightMapColor.rgb, tintedColor.a);
        } else {
            finalColor = vec4(tintedColor.rgb * lightMapColor.rgb, tintedColor.a);
        }
    } else {
        finalColor = vec4(tintedColor.rgb * lightMapColor.rgb, tintedColor.a);
    }

    fragColor = finalColor;
}