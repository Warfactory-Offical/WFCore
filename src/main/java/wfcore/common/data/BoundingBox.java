package wfcore.common.data;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record BoundingBox(IntCoord2 min, IntCoord2 max) {
}
