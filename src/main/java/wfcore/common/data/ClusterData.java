package wfcore.common.data;

import com.github.bsideup.jabel.Desugar;

import java.util.List;

@Desugar
public record ClusterData(List<IntCoord2> coordinates, IntCoord2 centerPoint, BoundingBox boundingBox
) {


}
