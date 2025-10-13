package rats3d

import "core:time"
delta_time :: proc() -> time.Duration {
	diff := time.tick_diff(global_state.last_frame_start, global_state.frame_start)
	return diff
}
