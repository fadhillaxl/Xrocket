package net.sf.openrocket.file.wavefrontobj.export.components;

import com.sun.istack.NotNull;
import net.sf.openrocket.file.wavefrontobj.CoordTransform;
import net.sf.openrocket.file.wavefrontobj.DefaultObj;
import net.sf.openrocket.file.wavefrontobj.ObjUtils;
import net.sf.openrocket.file.wavefrontobj.export.shapes.TubeExporter;
import net.sf.openrocket.logging.Warning;
import net.sf.openrocket.logging.WarningSet;
import net.sf.openrocket.rocketcomponent.FlightConfiguration;
import net.sf.openrocket.rocketcomponent.InstanceContext;
import net.sf.openrocket.rocketcomponent.LaunchLug;
import net.sf.openrocket.util.Coordinate;

public class LaunchLugExporter extends RocketComponentExporter<LaunchLug> {
    public LaunchLugExporter(@NotNull DefaultObj obj, FlightConfiguration config, @NotNull CoordTransform transformer,
                             LaunchLug component, String groupName, ObjUtils.LevelOfDetail LOD, WarningSet warnings) {
        super(obj, config, transformer, component, groupName, LOD, warnings);
    }

    @Override
    public void addToObj() {
        obj.setActiveGroupNames(groupName);

        final float outerRadius = (float) component.getOuterRadius();
        final float innerRadius = (float) component.getInnerRadius();
        final float length = (float) component.getLength();

        if (Double.compare(component.getThickness(), 0) == 0) {
            warnings.add(Warning.OBJ_ZERO_THICKNESS, component.getName());
        }

        // Generate the mesh
        for (InstanceContext context : config.getActiveInstances().getInstanceContexts(component)) {
            generateMesh(outerRadius, innerRadius, length, context);
        }
    }

    private void generateMesh(float outerRadius, float innerRadius, float length, InstanceContext context) {
        // Generate the mesh
        int startIdx = obj.getNumVertices();
        TubeExporter.addTubeMesh(obj, transformer, null, outerRadius, innerRadius, length, LOD);
        int endIdx = Math.max(obj.getNumVertices() - 1, startIdx);    // Clamp in case no vertices were added

        // Translate the mesh to the position in the rocket
        final Coordinate location = context.getLocation();
        ObjUtils.translateVerticesFromComponentLocation(obj, transformer, startIdx, endIdx, location);
    }
}
