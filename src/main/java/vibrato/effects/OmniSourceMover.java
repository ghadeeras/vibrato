package vibrato.effects;

import vibrato.dspunits.DspController;
import vibrato.dspunits.DspSource;
import vibrato.dspunits.DspSystem;
import vibrato.dspunits.filters.fir.VariableDelay;
import vibrato.functions.Linear;
import vibrato.interpolators.Interpolator;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class OmniSourceMover extends DspSystem implements DspSource<RealVector> {

    private final Source<RealVector> source;

    private OmniSourceMover(double minDistance, double maxDistance, RealVector position, RealValue audio) {
        super(44100);

        var audioSource = fromSource(audio);
        var audioSourcePos = fromSource(position);

        var audioSourceDistanceSquared = audioSourcePos.through(vectorLengthSquared);
        var audioSourceDistance = audioSourceDistanceSquared.through(scalarSquareRoot);
        var audioSourceDirection = audioSourcePos.through(vectorDivision, audioSourceDistance);

        var delay = VariableDelay.create(maxDistance, Interpolator.linear);
        var perceivedAudio = audioSource
            .through(delay, audioSourceDistance)
            .through(scalarDivision, audioSourceDistanceSquared.through(scalarMultiplication(1 / (minDistance * minDistance))));

        var earAngle = Math.PI / 6;
        var cos = Math.cos(earAngle);
        var sin = Math.sin(earAngle);
        var adapter = scalarFunction(Linear.linear(0.4, 0.6));
        var leftChannelWeight = audioSourceDirection.through(dotProduct(-cos, sin)).through(adapter);
        var rightChannelWeight = audioSourceDirection.through(dotProduct(+cos, sin)).through(adapter);

        this.source = join(
            perceivedAudio.through(scalarMultiplication, leftChannelWeight),
            perceivedAudio.through(scalarMultiplication, rightChannelWeight)
        );
    }

    @Override
    public RealVector output() {
        return source.output();
    }

    public static DspController<RealVector, RealValue, RealVector> create(double minDistance, double maxDistance) {
        return position -> audio -> new OmniSourceMover(minDistance, maxDistance, position, audio);
    }

}
