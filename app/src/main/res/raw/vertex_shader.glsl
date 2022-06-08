attribute vec4 v_Position;
uniform mat4 uMVPMatrix;
void main() {
    gl_Position = uMVPMatrix * v_Position;
}