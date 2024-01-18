import { useState } from 'react'

import axios from 'axios'

function UploadService() {
	const [file, setFile] = useState(null)

	const onFileChange = event => {
		setFile(event.target.files[0])
	}

	const onSubmit = event => {
		event.preventDefault()
		const formData = new FormData()
		formData.append('file', file)

		axios
			.post('http://localhost:8081/api/upload', formData)
			.then(response => {
				console.log('File uploaded successfully', response.data)
			})
			.catch(error => {
				console.error('There was an erro uploading the file', error)
			})
	}

	return (
		<div>
			<form onSubmit={onSubmit}>
				<input type='file' onChange={onFileChange} />
				<button>Upload!</button>
			</form>
		</div>
	)
}

export default UploadService
