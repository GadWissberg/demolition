package com.gadarts.vm;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VectorData {
	private final Image arrow;
	private final Vector2 vector = new Vector2(1, 0);


}
