package wfcore.common.render;

import com.modularmods.mcgltf.animation.InterpolatedChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class AnimationLoop {

        public final List<InterpolatedChannel> animation;

        @Getter @Setter
        private boolean loop = false;

        @Getter
        private boolean finished = false;

        private float startWorldTimeS = -1f;

        public AnimationLoop(List<InterpolatedChannel> animation) {
            this.animation = animation;
        }

        public void reset() {
            finished = false;
            startWorldTimeS = -1f;
        }

        public void update(float worldTimeS) {
            if (finished) return;

            if (startWorldTimeS < 0f) {
                startWorldTimeS = worldTimeS;
            }

            float localTime = worldTimeS - startWorldTimeS;
            float duration = getDuration();

            float t = localTime;

            if (t >= duration) {
                if (loop) {
                    t %= duration;
                    startWorldTimeS = worldTimeS - t;
                } else {
                    t = duration;
                    finished = true;
                }
            }

            for (InterpolatedChannel channel : animation) {
                channel.update(t);
            }
        }

        private float getDuration() {
            float max = 0f;
            for (InterpolatedChannel c : animation) {
                float[] keys = c.getKeys();
                max = Math.max(max, keys[keys.length - 1]);
            }
            return max;
        }
    }

