//This matrix member variable provides a hook to manipulate
// the coordinates of the objects that use this vertex shader
uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
void main() {
  gl_Position = uMVPMatrix * vPosition;
}