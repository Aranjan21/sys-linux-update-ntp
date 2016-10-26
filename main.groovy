#!groovy

/* Created by: Gupta, Kabir */

def call(def base){
	/* The 'call(def base)' method is the script (ie. workflow / pipeline / job) that will run
	   'call(def base)' must return a Map with two keys: 1) 'response' 2) 'message'
	   The only two valid values for 'response' are: 1) 'ok' 2) 'error'
	   The 'message' key must be a String if 'response' is 'error'
	   The 'message' key may be any serializable object if 'response' is 'ok' 
	   See the standards here: https://wiki/pages/viewpage.action?pageId=50965437 */

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

	return output
}

def input_validation() {
	def output = [
		'response': 'error',
		'message': ''
	]

	/* The following is an example of how to validate a job parameter named 'wf_address'

	if (wf_address == '') {
		output['message'] = 'Missing required parameter wf_address'
		return output
	}

	wf_address.replaceAll("\s", '').toLowerCase()
	this_base.set_str_param('wf_address', wf_address)
	output['response'] = 'ok'

	*/

	return output
}

return this
