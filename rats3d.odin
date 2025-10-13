package rats3d

import "core:log"
import "core:time"

Config_Flag :: enum {
	Panic_On_Error,
}

Config_Flags :: bit_set[Config_Flag]

Global_State :: struct {
	window:              Window,
	logger:              log.Logger,
	keys:                #sparse[Key]bool,
	last_keys:           #sparse[Key]bool,
	mouse_buttons:       #sparse[Mouse_Button]bool,
	last_mouse_buttons:  #sparse[Mouse_Button]bool,
	last_frame_start:    time.Tick,
	frame_start:         time.Tick,
	last_mouse_position: [2]f32,
	mouse_position:      [2]f32,
	panic_on_err:        bool,
}

@(private)
global_state: Global_State
