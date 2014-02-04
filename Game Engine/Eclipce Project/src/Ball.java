import src.GameClass;

public class Ball {

	private int x;
	private int y;
	private int speed;
	private int image;
	private GameClass game;

	public Ball(String path, int x, int y, int speed, GameClass game) {
		this.image = game.addImage("res/textures/ball.png");
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.game = game; 
	}

	public void blit() {
		game.drawImage(this.image, this.x, this.y);
	}

	public void moveLeft() {
		this.x = this.x - this.speed;
	}

	public void moveRight() {
		this.x =  this.x + this.speed;
	}

	public void moveUp() {
		this.y =  this.y - this.speed;
	}
	

	public void moveDown() {
		this.y =  this.y + this.speed;
	}

	public void screenWrap() {
		if (this.x < 0) {
			this.x = game.getScreenWidth();
		}
		if (this.x > game.getScreenWidth()) {
			this.x = 0;
		}

		if (this.y < 0) {
			this.y = game.getScreenHeight();
		}
		if (this.y > game.getScreenHeight()) {
			this.y = 0;
		}
	}
}