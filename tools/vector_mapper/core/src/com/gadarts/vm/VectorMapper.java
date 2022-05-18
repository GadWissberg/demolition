package com.gadarts.vm;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.IntStream;

public class VectorMapper extends ApplicationAdapter implements InputProcessor {

	public static final String ARROW_FILE_NAME = "arrow.png";
	private static final int MAP_SIZE = 10;
	private static final int CELL_SIZE = 64;
	private static final float GRID_Y_OFFSET = 10F;
	private static final float GRID_X_OFFSET = 10F;
	private static final Vector2 auxVector = new Vector2();
	private static final String KEY_VECTORS = "vectors";
	private static final String KEY_X = "x";
	private static final String KEY_Y = "y";
	private final Vector2 prevTouchPosition = new Vector2();
	private Stage stage;
	private ShapeRenderer shapeRenderer;
	private VectorData[][] vectorMatrix;
	private Texture arrowTexture;
	private Gson gson = new Gson();

	@Override
	public void create( ) {
		stage = new Stage();
		shapeRenderer = new ShapeRenderer();
		vectorMatrix = new VectorData[MAP_SIZE][MAP_SIZE];
		arrowTexture = new Texture(Gdx.files.getFileHandle(ARROW_FILE_NAME, Files.FileType.Internal));
		createVectorMatrix();
		Gdx.input.setInputProcessor(this);
	}

	private void createVectorMatrix( ) {
		for (int row = 0; row < MAP_SIZE; row++) {
			for (int col = 0; col < MAP_SIZE; col++) {
				Image arrow = new Image(arrowTexture);
				vectorMatrix[row][col] = new VectorData(arrow);
				float x = col * CELL_SIZE + CELL_SIZE / 2F - arrowTexture.getWidth() / 2F;
				float y = GRID_Y_OFFSET + row * CELL_SIZE + CELL_SIZE / 2F - arrowTexture.getHeight() / 2F;
				arrow.setOrigin(arrowTexture.getWidth() / 2F, arrowTexture.getHeight() / 2f);
				arrow.setPosition(x, y);
				stage.addActor(arrow);
			}
		}
	}

	@Override
	public void render( ) {
		ScreenUtils.clear(0, 0, 0, 1);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		drawGrid();
		shapeRenderer.end();
		stage.act();
		stage.draw();
	}

	private void drawGrid( ) {
		IntStream.rangeClosed(0, MAP_SIZE)
				.forEach(i -> shapeRenderer.line(
						GRID_X_OFFSET + i * CELL_SIZE,
						GRID_Y_OFFSET,
						GRID_X_OFFSET + i * CELL_SIZE,
						GRID_Y_OFFSET + MAP_SIZE * CELL_SIZE));
		IntStream.rangeClosed(0, MAP_SIZE)
				.forEach(i -> shapeRenderer.line(
						GRID_X_OFFSET,
						GRID_Y_OFFSET + i * CELL_SIZE,
						GRID_X_OFFSET + MAP_SIZE * CELL_SIZE,
						GRID_Y_OFFSET + i * CELL_SIZE));
	}

	@Override
	public void dispose( ) {
		stage.dispose();
		shapeRenderer.dispose();
		arrowTexture.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.SPACE) {
			JsonObject map = new JsonObject();
			JsonArray vectors = new JsonArray();
			for (int row = 0; row < MAP_SIZE; row++) {
				JsonArray rowArray = new JsonArray();
				vectors.add(rowArray);
				for (int col = 0; col < MAP_SIZE; col++) {
					JsonObject vectorJson = new JsonObject();
					VectorData vectorData = vectorMatrix[row][col];
					Vector2 vector = vectorData.getVector();
					vectorJson.addProperty(KEY_X, vector.x);
					vectorJson.addProperty(KEY_Y, vector.y);
					rowArray.add(vectorJson);
				}
			}
			map.add(KEY_VECTORS, vectors);
			try (Writer writer = new FileWriter("Output.json")) {
				Gson gson = new GsonBuilder().create();
				gson.toJson(map, writer);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenY = (int) (Gdx.graphics.getHeight() - screenY - GRID_Y_OFFSET);
		prevTouchPosition.set(screenX, screenY);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		int col = (int) ((screenX - GRID_X_OFFSET) / CELL_SIZE);
		screenY = (int) (Gdx.graphics.getHeight() - screenY - GRID_Y_OFFSET);
		int row = (int) (((screenY)) / CELL_SIZE);
		if ((col >= 0 && col < MAP_SIZE) && row >= 0 && row < MAP_SIZE) {
			VectorData vectorData = vectorMatrix[row][col];
			Vector2 vector2 = vectorData.getVector();
			vector2.set(auxVector.set(screenX, screenY).sub(prevTouchPosition));
			Image arrow = vectorData.getArrow();
			arrow.setRotation(vector2.angleDeg());
			prevTouchPosition.set(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
