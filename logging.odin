package rats3d

import "core:log"

create_default_logger :: proc(panic_on_err: bool) -> log.Logger {
	global_state.panic_on_err = true
	return log.create_console_logger(
		ident = "RATS3D",
		opt = {.Level, .Terminal_Color, .Short_File_Path, .Line},
	)
}

log_error :: proc(fmt: string, args: ..any, loc := #caller_location) {
	context.logger = global_state.logger

	if global_state.panic_on_err {
		log.panicf(fmt, ..args, location = loc)
	} else {
		log.errorf(fmt, ..args, location = loc)
	}
}

log_warning :: proc(fmt: string, args: ..any, loc := #caller_location) {
	context.logger = global_state.logger

	log.warn(fmt, args, location = loc)
}
