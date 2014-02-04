import src.GameClass;

public class Driver {

	public static void main(String args[]) {
		GameClass game = new GameClass(800, 600, "Hello");
		int music = game.addSound("res/sounds/soundtrack.wav");
		
		Ball ball = new Ball("res/textures/ball.png", 250, 250, 1, game);
		

		game.playSound(music, -15, false);

		while(!game.keyboard.escape) {
			if (game.tickUpdate) {  
				if (game.keyboard.left) {
					ball.moveLeft();
				}
				if (game.keyboard.right) {
					ball.moveRight();
				}
				if (game.keyboard.up) {
					ball.moveUp();
				}
				if (game.keyboard.down) {
					ball.moveDown();
				} 

				ball.screenWrap();
			}

			game.drawText("Uptime: " + String.valueOf(game.upTime), "cyan", 12,12, 14);
			game.drawText("FPS: " + String.valueOf(game.frames), "cyan", 12,24, 14);
			game.drawText("Ticks: " + String.valueOf(game.ticks), "cyan", 12,36, 14);
			ball.blit();
            game.pause();
		}

		game.stop();

	}
	
}