#!groovy

/* Created by: Gupta, Kabir */

def call(def base) {
	this_base = base
	def glob_objs = base.get_glob_objs()
	def output = [
		'response': 'error',
		'message': ''
	]

	/* Validate and sanitize the input */
	def input_validation = this.input_validation()
	if (input_validation['response'] == 'error') {
		return input_validation
	}

	/* Read the bash file resynchonrize the NTP offset */
	def synch_script = base.read_wf_file('sys-linux-update-ntp', 'resynch_time.sh')
	if(synch_script['response'] == 'error'){
		return synch_script
	}
	synch_script = synch_script['message']

	/* Read the bash file that checks the time offset */
	offset_check = base.read_wf_file('sys-linux-update-ntp', 'offset_check.sh')
	if(offset_check['response'] == 'error'){
		return offset_check
	}
	offset_check = offset_check['message']

	/* Create change ticket */
	def chg_desc = "Resynch NTP Offset: ${wf_address}\n"
	def chg_ticket = base.create_chg_ticket(
		wf_address,
		'Resynch NTP Offset',
		chg_desc,
		'Network Operations Center',
		wf_requester
	)
	if (chg_ticket['response'] == 'error') {
	    output['message'] = "FAILURE: ${wf_address} time wasn't resynchronized because a change ticket wasn't created: ${chg_ticket['message']}"
	    return output
	}

	/* Update the ticket with the current NTP offset */
	def ntp_offset_before = this.ntp_offset()

	/* Run the bash script to resynch the time */
	def synch_script_output = base.run_shellscript('Running script to re-synch the NTP offset',
		synch_script,
		base.get_cred_id(wf_address),
		[
			'_address_': wf_address
		]
	)

	Boolean success = true
	if (synch_script_output['response'] == 'ok') {
		output['response'] = 'ok'

		chg_desc = "SUCCESS: ${wf_address} time offset was resynchronized.\n"
		base.update_chg_ticket_desc(chg_desc)

		/* Update the ticket with the current NTP offset */
		def ntp_offset_after = this.ntp_offset()

		ntp_offset_before = ntp_offset_before['message']
		ntp_offset_after = ntp_offset_after['message']

		if (ntp_offset_before == ntp_offset_after) {
			chg_desc = "NOTE: The time offset before/after the resync is still the same because the changes haven't been picked up yet.\n"
			output['message'] = "${wf_address} NTP offset was resynchronized but changes haven't been picked up yet."
		}
	} else {
		/* Update ticket/output if validation is successfull */
		success = false
		chg_desc = "FAILURE:\n${wf_address} time offset was not resynchronized.\n${synch_script_output['message']}\n"
		output['response'] = 'error'
		output['message'] = "${wf_address} NTP offset was not resynchronized ${synch_script_output['message']}."
	}

	base.update_chg_ticket_desc(chg_desc)
	base.close_chg_ticket(success)
	return output
}

/* Run the bash script to check time offset */
def ntp_offset() {

	def offset_check_output = this_base.run_shellscript('Running script to check time offset',
		offset_check,
		this_base.get_cred_id(wf_address),
		[
			'_address_': wf_address
		]
	)

	if (offset_check_output['response'] == 'ok') {
		chg_desc = "The current time offset is: ${offset_check_output['message']} milliseconds.\n"
		this_base.update_chg_ticket_desc(chg_desc)
		return offset_check_output
	}
}

def input_validation() {
	def output = [
		'response': 'error',
		'message': ''
	]

	if (wf_address == '') {
		output['message'] = 'Missing required parameter wf_address'
		return output
	}

	wf_address = wf_address.replaceAll('\\s', '').toLowerCase()
	this_base.set_str_param('wf_address', wf_address)
	output['response'] = 'ok'

	return output
}

return this
