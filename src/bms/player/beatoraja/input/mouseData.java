package bms.player.beatoraja.input;

public class mouseData {
	private static int mousex;
	private static int mousey;
	private static int mousebutton;
	private static boolean mousepressed;
	private static boolean mousedragged;
	private static boolean mouseMoved;

	
	public static int getMouseX() {
		return mousex;
	}
	
	public static void setMouseX(int x) {
		mousex = x;
	}

	public static int getMouseY() {
		return mousey;
	}
	
	public static void setMouseY(int y) {
		mousex = y;
	}

	public static int getMouseButton() {
		return mousebutton;
	}
	
	public static void setMouseButton(int button) {
		mousebutton = button;
	}

	public static boolean isMousePressed() {
		return mousepressed;
	}

	public static void setMousePressed(boolean isPressed) {
		mousepressed = isPressed;
	}

	public static boolean isMouseDragged() {
		return mousedragged;
	}

	public static void setMouseDragged(boolean isDragged) {
		mousedragged = isDragged;
	}

	public static boolean isMouseMoved() {
		return mouseMoved;
	}

	public static void setMouseMoved(boolean mouseMoved) {
		mouseMoved = mouseMoved;
	}
}